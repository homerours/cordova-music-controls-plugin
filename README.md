# Cordova Music Controls Plugin
Music controls for Cordova applications.

## Supported platforms
- Android (4.1+)
- Windows (10+)

For iOS, see [shi11/RemoteControls](https://github.com/shi11/RemoteControls).

## Installation
`cordova plugin add https://github.com/homerours/cordova-music-controls-plugin`

## Methods
- Create the media controls:
```javascript
MusicControls.create({
    track     : 'Time is Running Out',
	artist    : 'Muse',
    cover     : 'albums/absolution.jpg',
    isPlaying : true
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
## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Quirks
* Windows currently only supports locally stored covers.
* This plugin is still under development: it might not be considered for production apps.
