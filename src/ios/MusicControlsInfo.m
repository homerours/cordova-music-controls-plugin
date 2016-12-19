 //
//  MusicControlsInfo.m
//  
//
//  Created by Juan Gonzalez on 12/17/16.
//
//

#import "MusicControlsInfo.h"

@implementation MusicControlsInfo : NSObject

- (id) initWithDictionary: (NSDictionary *) dictionary {
    
    if (self = [super init]) {
        [self setArtist: [dictionary objectForKey:@"artist"]];
        [self setTrack: [dictionary objectForKey:@"track"]];
        [self setAlbum: [dictionary objectForKey:@"album"]];
        [self setTicker: [dictionary objectForKey:@"ticker"]];
        [self setCover: [dictionary objectForKey:@"cover"]];
        [self setDuration: [[dictionary objectForKey:@"duration"] integerValue]];
        [self setElapsed: [[dictionary objectForKey:@"elapsed"] integerValue]];
        [self setIsPlaying: [dictionary objectForKey:@"is_playing"]];
        [self setHasPrev: [dictionary objectForKey:@"has_prev"]];
        [self setHasNext: [dictionary objectForKey:@"has_next"]];
        [self setDismissable: [dictionary objectForKey:@"dismissable"]];
    }
    
    return self;
}

@end
