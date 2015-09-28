# Cordova Music Controller Plugin
Music controls for Cordova applications.

## Supported platforms
- Android (4.1+)
- Windows (10+)

For iOS, see [shi11/RemoteControls](https://github.com/shi11/RemoteControls).

## Installation
`cordova plugin add https://github.com/homerours/cordova-music-controls-plugin`

## Methods
- Create the media controller:
```javascript
MusicController.create({
    track     : 'Time is Running Out',
	artist    : 'Muse',
    cover     : 'albums/absolution.jpg',
    isPlaying : true
}, onSuccess, onError);
```

- Destroy the media controller:
```javascript
MusicController.destroy(onSuccess, onError);
```

- Subscribe events to the media controller:
```javascript
function events(action) {
	switch(action) {
		case 'music-controller-next':
			// Do something
			break;
		case 'music-controller-previous':
			// Do something
			break;
		case 'music-controller-pause':
			// Do something
			break;
		case 'music-controller-play':
			// Do something
			break;

		// Headset events (Android only)
		case 'music-controller-media-button' :
			// Do something
			break;
		case 'music-controller-headset-unplugged':
			// Do something
			break;
		case 'music-controller-headset-plugged':
			// Do something
			break;
		default:
			break;
	}
}

// Register callback
MusicController.subscribe(events);

// Start listening for events
// The plugin will run the events function each time an event is fired
MusicController.listen();
```
## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Quirks
* Cordova 5.0 or higher is required for Windows support.
* Windows currently only supports locally stored covers.
* This plugin is still under development: it might not be considered for production apps.


## Screenshots
![Android](http://i.imgur.com/Qe1a8ZJ.png)
![Windows](http://i.imgur.com/Y4HsM0s.png)
