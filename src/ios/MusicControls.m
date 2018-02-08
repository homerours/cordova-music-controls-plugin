//
//  MusicControls.m
//  
//
//  Created by Juan Gonzalez on 12/16/16.
//  Updated by Gaven Henry on 11/7/17 for iOS 11 compatibility & new features
//
//

#import "MusicControls.h"
#import "MusicControlsInfo.h"

//save the passed in info globally so we can configure the enabled/disabled commands and skip intervals
MusicControlsInfo * musicControlsSettings;

@implementation MusicControls

- (void) create: (CDVInvokedUrlCommand *) command {
    NSDictionary * musicControlsInfoDict = [command.arguments objectAtIndex:0];
    MusicControlsInfo * musicControlsInfo = [[MusicControlsInfo alloc] initWithDictionary:musicControlsInfoDict];
    musicControlsSettings = musicControlsInfo;
    
    if (!NSClassFromString(@"MPNowPlayingInfoCenter")) {
        return;
    }
    MPNowPlayingInfoCenter * nowPlayingInfoCenter =  [MPNowPlayingInfoCenter defaultCenter];
    NSDictionary * nowPlayingInfo = nowPlayingInfoCenter.nowPlayingInfo;
    NSMutableDictionary * updatedNowPlayingInfo = [NSMutableDictionary dictionaryWithDictionary:nowPlayingInfo];
    
    NSNumber * duration = [NSNumber numberWithInt:[musicControlsInfo duration]];
    NSNumber * elapsed = [NSNumber numberWithInt:[musicControlsInfo elapsed]];
    NSNumber * playbackRate = [NSNumber numberWithBool:[musicControlsInfo isPlaying]];
    
    
    [updatedNowPlayingInfo setObject:[musicControlsInfo artist] forKey:MPMediaItemPropertyArtist];
    [updatedNowPlayingInfo setObject:[musicControlsInfo track] forKey:MPMediaItemPropertyTitle];
    [updatedNowPlayingInfo setObject:[musicControlsInfo album] forKey:MPMediaItemPropertyAlbumTitle];
    [updatedNowPlayingInfo setObject:duration forKey:MPMediaItemPropertyPlaybackDuration];
    [updatedNowPlayingInfo setObject:elapsed forKey:MPNowPlayingInfoPropertyElapsedPlaybackTime];
    [updatedNowPlayingInfo setObject:playbackRate forKey:MPNowPlayingInfoPropertyPlaybackRate];
    
    if (@available(iOS 11.0, *)) {
        if ([musicControlsInfo isPlaying]){
            [nowPlayingInfoCenter setPlaybackState:MPNowPlayingPlaybackStatePlaying];
            
        }else{
            [nowPlayingInfoCenter setPlaybackState:MPNowPlayingPlaybackStatePaused];
            
        }
    }
    
    [nowPlayingInfoCenter setNowPlayingInfo:updatedNowPlayingInfo];
    
    [self.commandDelegate runInBackground:^{
        
        MPMediaItemArtwork * mediaItemArtwork = [self createCoverArtwork:[musicControlsInfo cover]];
        
        if (mediaItemArtwork != nil ) {
            UIImage *newImage = [[updatedNowPlayingInfo objectForKey:@"artwork"] imageWithSize:CGSizeMake(1, 1)];
            UIImage *oldImage = [mediaItemArtwork imageWithSize:CGSizeMake(1, 1)];
            if ([newImage isEqual:oldImage] == NO){
                [updatedNowPlayingInfo setObject:mediaItemArtwork forKey:MPMediaItemPropertyArtwork];
            }
        }
        [nowPlayingInfoCenter setNowPlayingInfo:updatedNowPlayingInfo] ;
        
        
    }];
    [self registerMusicControlsEventListener];
}

- (void) updateIsPlaying: (CDVInvokedUrlCommand *) command {
    NSDictionary * musicControlsInfoDict = [command.arguments objectAtIndex:0];
    MusicControlsInfo * musicControlsInfo = [[MusicControlsInfo alloc] initWithDictionary:musicControlsInfoDict];
    NSNumber * elapsed = [NSNumber numberWithDouble:[musicControlsInfo elapsed]];
    NSNumber * playbackRate = [NSNumber numberWithBool:[musicControlsInfo isPlaying]];
    
    if (!NSClassFromString(@"MPNowPlayingInfoCenter")) {
        return;
    }

    MPNowPlayingInfoCenter * nowPlayingCenter = [MPNowPlayingInfoCenter defaultCenter];
    NSMutableDictionary * updatedNowPlayingInfo = [NSMutableDictionary dictionaryWithDictionary:nowPlayingCenter.nowPlayingInfo];
    if (@available(iOS 11.0, *)) {
        if ([musicControlsInfo isPlaying]){
            [nowPlayingCenter setPlaybackState:MPNowPlayingPlaybackStatePlaying];
            
        }else{
            [nowPlayingCenter setPlaybackState:MPNowPlayingPlaybackStatePaused];
            
        }
    }
    [updatedNowPlayingInfo setObject:elapsed forKey:MPNowPlayingInfoPropertyElapsedPlaybackTime];
    [updatedNowPlayingInfo setObject:playbackRate forKey:MPNowPlayingInfoPropertyPlaybackRate];
    nowPlayingCenter.nowPlayingInfo = updatedNowPlayingInfo;
}

// this was performing the full function of updateIsPlaying and just adding elapsed time update as well
// moved the elapsed update into updateIsPlaying and made this just pass through to reduce code duplication
- (void) updateElapsed: (CDVInvokedUrlCommand *) command {
    [self updateIsPlaying:(command)];
}

- (void) destroy: (CDVInvokedUrlCommand *) command {
    [self deregisterMusicControlsEventListener];
}

- (void) watch: (CDVInvokedUrlCommand *) command {
    [self setLatestEventCallbackId:command.callbackId];
}

- (MPMediaItemArtwork *) createCoverArtwork: (NSString *) coverUri {
    UIImage * coverImage = nil;
    
    if (coverUri == nil) {
        return nil;
    }
    
    if ([coverUri hasPrefix:@"http://"] || [coverUri hasPrefix:@"https://"]) {
        NSURL * coverImageUrl = [NSURL URLWithString:coverUri];
        NSData * coverImageData = [NSData dataWithContentsOfURL: coverImageUrl];
        
        coverImage = [UIImage imageWithData: coverImageData];
    }
    else if ([coverUri hasPrefix:@"file://"]) {
        NSString * fullCoverImagePath = [coverUri stringByReplacingOccurrencesOfString:@"file://" withString:@""];
        
        if ([[NSFileManager defaultManager] fileExistsAtPath: fullCoverImagePath]) {
            coverImage = [[UIImage alloc] initWithContentsOfFile: fullCoverImagePath];
        }
    }
    else if (![coverUri isEqual:@""]) {
        NSString * baseCoverImagePath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        NSString * fullCoverImagePath = [NSString stringWithFormat:@"%@%@", baseCoverImagePath, coverUri];
    
        if ([[NSFileManager defaultManager] fileExistsAtPath:fullCoverImagePath]) {
            coverImage = [UIImage imageNamed:fullCoverImagePath];
        }
    }
    else {
        coverImage = [UIImage imageNamed:@"none"];
    }
    
    return [self isCoverImageValid:coverImage] ? [[MPMediaItemArtwork alloc] initWithImage:coverImage] : nil;
}

- (bool) isCoverImageValid: (UIImage *) coverImage {
    return coverImage != nil && ([coverImage CIImage] != nil || [coverImage CGImage] != nil);
}

//Handle seeking with the progress slider on lockscreen or control center
- (MPRemoteCommandHandlerStatus)changedThumbSliderOnLockScreen:(MPChangePlaybackPositionCommandEvent *)event {
    NSString * seekTo = [NSString stringWithFormat:@"{\"message\":\"music-controls-seek-to\",\"position\":\"%f\"}", event.positionTime];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:seekTo];
    pluginResult.associatedObject = @{@"position":[NSNumber numberWithDouble: event.positionTime]};
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
    return MPRemoteCommandHandlerStatusSuccess;
}

//Handle the skip forward event
- (void) skipForwardEvent:(MPSkipIntervalCommandEvent *)event {
    NSString * action = @"music-controls-skip-forward";
    NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
}

//Handle the skip backward event
- (void) skipBackwardEvent:(MPSkipIntervalCommandEvent *)event {
    NSString * action = @"music-controls-skip-backward";
    NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
}

//If MPRemoteCommandCenter is enabled for any function we must enable it for all and register a handler
//So if we want to use the new scrubbing support in the lock screen we must implement dummy handlers
//for those functions that we already deal with through notifications (play, pause, skip etc)
//otherwise those remote control actions will be disabled
- (void) remoteEvent:(MPRemoteCommandEvent *)event {
    return;
}

- (void) nextTrackEvent:(MPRemoteCommandEvent *)event {
    NSString * action = @"music-controls-next";
    NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
}

- (void) prevTrackEvent:(MPRemoteCommandEvent *)event {
    NSString * action = @"music-controls-previous";
    NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
}

- (void) pauseEvent:(MPRemoteCommandEvent *)event {
    NSString * action = @"music-controls-pause";
    NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
}

- (void) playEvent:(MPRemoteCommandEvent *)event {
    NSString * action = @"music-controls-play";
    NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
}

//Handle all other remote control events
- (void) handleMusicControlsNotification: (NSNotification *) notification {
    UIEvent * receivedEvent = notification.object;
    
    if ([self latestEventCallbackId] == nil) {
        return;
    }
    
    if (receivedEvent.type == UIEventTypeRemoteControl) {
        NSString * action;

        switch (receivedEvent.subtype) {
            case UIEventSubtypeRemoteControlTogglePlayPause:
                action = @"music-controls-toggle-play-pause";
                break;
                
            case UIEventSubtypeRemoteControlPlay:
                action = @"music-controls-play";
                break;
                
            case UIEventSubtypeRemoteControlPause:
                action = @"music-controls-pause";
                break;
                
            case UIEventSubtypeRemoteControlPreviousTrack:
                action = @"music-controls-previous";
                break;
                
            case UIEventSubtypeRemoteControlNextTrack:
                action = @"music-controls-next";
                break;
                
            case UIEventSubtypeRemoteControlStop:
                action = @"music-controls-destroy";
                break;
                
            default:
                action = nil;
                break;
        }
        
        if(action == nil){
            return;
        }
        
        NSString * jsonAction = [NSString stringWithFormat:@"{\"message\":\"%@\"}", action];
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonAction];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:[self latestEventCallbackId]];
    }
}

//There are only 3 button slots available so next/prev track and skip forward/back cannot both be enabled
//skip forward/back will take precedence if both are enabled
- (void) registerMusicControlsEventListener {
    [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleMusicControlsNotification:) name:@"musicControlsEventNotification" object:nil];
    
    //register required event handlers for standard controls
    MPRemoteCommandCenter *commandCenter = [MPRemoteCommandCenter sharedCommandCenter];
    [commandCenter.playCommand setEnabled:true];
    [commandCenter.playCommand addTarget:self action:@selector(playEvent:)];
    [commandCenter.pauseCommand setEnabled:true];
    [commandCenter.pauseCommand addTarget:self action:@selector(pauseEvent:)];
    if(musicControlsSettings.hasNext){
        [commandCenter.nextTrackCommand setEnabled:true];
        [commandCenter.nextTrackCommand addTarget:self action:@selector(nextTrackEvent:)];
    }
    if(musicControlsSettings.hasPrev){
        [commandCenter.previousTrackCommand setEnabled:true];
        [commandCenter.previousTrackCommand addTarget:self action:@selector(prevTrackEvent:)];
    }

    //Some functions are not available in earlier versions
    if(floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_9_0){
        if(musicControlsSettings.hasSkipForward){
            commandCenter.skipForwardCommand.preferredIntervals = @[@(musicControlsSettings.skipForwardInterval)];
            [commandCenter.skipForwardCommand setEnabled:true];
            [commandCenter.skipForwardCommand addTarget: self action:@selector(skipForwardEvent:)];
        }
        if(musicControlsSettings.hasSkipBackward){
            commandCenter.skipBackwardCommand.preferredIntervals = @[@(musicControlsSettings.skipForwardInterval)];
            [commandCenter.skipBackwardCommand setEnabled:true];
            [commandCenter.skipBackwardCommand addTarget: self action:@selector(skipBackwardEvent:)];
        }
        if(musicControlsSettings.hasScrubbing){
            [commandCenter.changePlaybackPositionCommand setEnabled:true];
            [commandCenter.changePlaybackPositionCommand addTarget:self action:@selector(changedThumbSliderOnLockScreen:)];
        }
    }
}

- (void) deregisterMusicControlsEventListener {
    [[UIApplication sharedApplication] endReceivingRemoteControlEvents];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"receivedEvent" object:nil];
    
    MPRemoteCommandCenter *commandCenter = [MPRemoteCommandCenter sharedCommandCenter];
    [commandCenter.nextTrackCommand removeTarget:self];
    [commandCenter.previousTrackCommand removeTarget:self];
    
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_9_0) {
        [commandCenter.changePlaybackPositionCommand setEnabled:false];
        [commandCenter.changePlaybackPositionCommand removeTarget:self action:NULL];
        [commandCenter.skipForwardCommand removeTarget:self];
        [commandCenter.skipBackwardCommand removeTarget:self];
    }
    
    [self setLatestEventCallbackId:nil];
}

- (void) dealloc {
    [self deregisterMusicControlsEventListener];
}

@end
