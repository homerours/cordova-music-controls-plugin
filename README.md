# cordova-plugin-music-controller
Interactive multimedia controls

## Supported platforms
- Android (4.1+)
- iOS (under development)
- Windows (10+)

## Installation
`cordova plugin add https://github.com/filfat-Studios-AB/cordova-plugin-music-controller`

## Methods
Create the media controller:
```javascript
MusicController.create({
    track: 'Speak Now',
	artist: 'Taylor Swift',
    cover: 'albums/speak-now.jpg',
    isPlaying: true
}, onSuccess, onError);
```

Destroy the media controller:
```javascript
MusicController.destory(onSuccess, onError);
```

Subscribe to the media controller events:
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
		default:
			break;
	}
}
MusicController.subscribe(events, onSuccess, onError);
```

##Quirks
* Currently you need to subscribe again everytime an action has fired.
* Cordova 5.0 or higher is required for Windows support.
* Windows currently only supports locally stored covers.
* This plugin is still under development which means that it's not yet "production ready".
