# Cordova Music Controls Plugin

<img src='https://github.com/homerours/cosmic/blob/master/screenshots/notification.png' width='564' height='342'>

Music controls for Cordova applications. Display a 'media' notification with play/pause, previous, next buttons, allowing the user to control the play. Handle also headset event (plug, unplug, headset button).

## Supported platforms
- Android (4.1+)
- Windows (10+, by [filfat](https://github.com/filfat))
- iOS (by [0505gonzalez](https://github.com/0505gonzalez))

## Installation
`cordova plugin add https://github.com/homerours/cordova-music-controls-plugin`

## Methods
- Create the media controls:
```javascript
MusicControls.create({
    track       : 'Time is Running Out',		// optional, default : ''
	  artist      : 'Muse',						// optional, default : ''
    cover       : 'albums/absolution.jpg',		// optional, default : nothing
	// cover can be a local path (use fullpath 'file:///storage/emulated/...', or only 'my_image.jpg' if my_image.jpg is in the www folder of your app)
	//			 or a remote url ('http://...', 'https://...', 'ftp://...')
  isPlaying   : true,							// optional, default : true
	dismissable : true,							// optional, default : false

	// hide previous/next/close buttons:
	hasPrev   : false,		// show previous button, optional, default: true
	hasNext   : false,		// show next button, optional, default: true
	hasClose  : true,		// show close button, optional, default: false

  // iOS only, optional
  album       : 'Absolution'     // optional, default: ''
  duration : 60, // optional, default: 0
  elapsed : 10, // optional, default: 0

	// Android only, optional
	// text displayed in the status bar when the notification (and the ticker) are updated
	ticker	  : 'Now playing "Time is Running Out"'
}, onSuccess, onError);
```

- Destroy the media controller:
```javascript
MusicControls.destroy(onSuccess, onError);
```

- Subscribe events to the media controller:
```javascript
function events(action) {
	switch(action) {
		case 'music-controls-next':
			// Do something
			break;
		case 'music-controls-previous':
			// Do something
			break;
		case 'music-controls-pause':
			// Do something
			break;
		case 'music-controls-play':
			// Do something
			break;
		case 'music-controls-destroy':
			// Do something
			break;

    // External controls (iOS only)
    case 'music-controls-toggle-play-pause' :
			// Do something
			break;

		// Headset events (Android only)
		case 'music-controls-media-button' :
			// Do something
			break;
		case 'music-controls-headset-unplugged':
			// Do something
			break;
		case 'music-controls-headset-plugged':
			// Do something
			break;
		default:
			break;
	}
}

// Register callback
MusicControls.subscribe(events);

// Start listening for events
// The plugin will run the events function each time an event is fired
MusicControls.listen();
```

- Toggle play/pause:
```javascript
MusicControls.updateIsPlaying(true); // toggle the play/pause notification button
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
