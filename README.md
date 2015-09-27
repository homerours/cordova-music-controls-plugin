# Cordova Music Controls Plugin
Music controls for Cordova applications.

## Supported platforms
- Android (4.1+)
- Windows (10+)

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
	switch(action){
		case 'music-controller-next':
			//Do something
			break;
		case 'music-controller-previous':
			//Do something
			break;
		case 'music-controller-pause':
			//Do something
			break;
		case 'music-controller-play':
			//Do something
			break;

		// Headset events (Android only)
		case 'music-controller-media-button' :
			//Do something
			break;
		case 'music-controller-headset-unplugged':
			//Do something
			break;
		case 'music-controller-headset-plugged':
			//Do something
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

##Quirks
* Cordova 5.0 or higher is required for Windows support.
* Windows currently only supports locally stored covers.
* This plugin is still under development which means that it's not yet "production ready".


##Screenshots
![Android](http://i.imgur.com/Qe1a8ZJ.png)
![Windows](http://i.imgur.com/Y4HsM0s.png)
