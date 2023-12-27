class WorldObject {
	
	constructor(state) {
		this.focused = false;
		this.active = false;
		this.dragging = false;
	
		const split = state.split(",");
		this.id = split[0];
		this.type = parseInt(split[1]);
		this.size = parseInt(split[2]);
		this.x = parseInt(split[3]);
		this.y = parseInt(split[4]);
		this.angle = parseInt(split[5]);	
	}
	
	updateLocation(x, y) {
		this.x = x;
		this.y = y;
	}		
	
}

class GameWorld {
	
	constructor(canvas) {
		this.worldObjects = new Map();
		
		this.canvas = canvas;
		this.canvasContext = canvas.getContext("2d");
		this.canvasSize = { x: 600, y: 600 };
		this.camera = { x: 0, y: 0 }
		this.displayGridStep = 100;
		this.worldRatio = 0.2; // pixels in 1 world point
		this.isDraggable = false;
		this.mouseDownPoint;
		
		this.canvasContext.canvas.width  = this.canvasSize.x;
		this.canvasContext.canvas.height = this.canvasSize.y;
		this.initHandlers();
		
		this.callbackViewDragged;
	}

	updateSize(width, height) {
	    this.canvas.width = this.canvasContext.canvas.width = this.canvasSize.x = width;
        this.canvas.height = this.canvasContext.canvas.height = this.canvasSize.y = height;
	}
	
	toCanvasPoint(worldX, worldY) {
		return { 
			x: this.canvasSize.x / 2 + this.camera.x * this.worldRatio + worldX  * this.worldRatio,
			y: this.canvasSize.y / 2 + this.camera.y * this.worldRatio + worldY  * this.worldRatio
		};
	}

	toWorldPoint(canvasX, canvasY) {
		return {
			x: (canvasX - this.canvasSize.x / 2 - this.camera.x * this.worldRatio) / this.worldRatio,
			y: (canvasY - this.canvasSize.y / 2 - this.camera.y * this.worldRatio) / this.worldRatio
		};
	}
	
	drawObject(gameObject) {
		const displaySize = gameObject.size * this.worldRatio;
		var canvasPoint = this.toCanvasPoint(gameObject.x, gameObject.y);
		this.canvasContext.fillStyle = gameObject.focused ?  "#ff0000" : gameObject.active ? "#00ff00" : "#000000";
		this.canvasContext.fillRect(canvasPoint.x - displaySize / 2, canvasPoint.y - displaySize / 2, displaySize, displaySize);
	}
	
	draw() {
		this.canvasContext.clearRect(0, 0, canvas.width, canvas.height);
	
		var gridStep = this.displayGridStep * this.worldRatio;
		// Draw grid
		var worldPoint = this.toCanvasPoint(0, 0);
		
		this.canvasContext.strokeStyle = "#a1a1a1";
		this.canvasContext.beginPath();
		var dx = worldPoint.x;
		for (let i = dx; i < this.canvasSize.x; i += gridStep) {
			this.canvasContext.moveTo(i, 0);
			this.canvasContext.lineTo(i, this.canvasSize.y);
		}
		var dy = worldPoint.y;
		for (let i = dy; i < this.canvasSize.y; i += gridStep) {
			this.canvasContext.moveTo(0, i);
			this.canvasContext.lineTo(this.canvasSize.x, i);
		}
		this.canvasContext.stroke();
		
		// Draw objects
		for (let object of this.worldObjects.values()) {
			this.drawObject(object);
		}
	}
	
	initHandlers() {
		this.canvas.addEventListener('mousedown', e => {
			this.handleMouseDown(e);
		});
		this.canvas.addEventListener('mousemove', e => {
			this.handleMouseMove(e);
		});
		this.canvas.addEventListener('mouseup', e => {
			this.handleMouseUp(e);
		});
		this.canvas.addEventListener('mouseout', e => {
			this.handleMouseUp(e);
		});
		this.canvas.addEventListener('wheel', e => {
			this.handleMouseWheel(e);
		}, false);	
	}
	
	getMouseCoords(canvas, event) {
		let canvasCoords = canvas.getBoundingClientRect()
		return {
			x: event.pageX - canvasCoords.left,
			y: event.pageY - canvasCoords.top
		}
	}

	cursorInRect(mouseX, mouseY, rectX, rectY, rectW, rectH) {
		let xLine = mouseX >= rectX && mouseX <= rectX + rectW
		let yLine = mouseY >= rectY && mouseY <= rectY + rectH
		return xLine && yLine
	}
	
	handleMouseDown(e) {
		let mouse = this.getMouseCoords(canvas, e)
		this.mouseDownPoint = {
			mouseX: mouse.x,
			mouseY: mouse.y,
			cameraX: this.camera.x,
			cameraY: this.camera.y
		}		
		for (var obj of this.worldObjects.values()) {
			var objPoint = this.toCanvasPoint(obj.x, obj.y);
			var objSize = obj.size * this.worldRatio;
			obj.active = obj.focused;
			obj.dragging = obj.active;
		}	
		this.isDraggable = true;
		this.draw();
	}
	
	handleMouseMove(e) {
		let mouse = this.getMouseCoords(canvas, e)			
		if (this.isDraggable) {
			var selectedObject;
			for (var obj of this.worldObjects.values()) {
				if (obj.active == true) {
					selectedObject = obj;
					break;
				}
			}	
			if (typeof selectedObject !== 'undefined') {
				// Drag selected object
				var worldLocation = this.toWorldPoint(mouse.x, mouse.y);
				selectedObject.updateLocation(worldLocation.x, worldLocation.y);
			} else {
				// Drag game world
				var xDif = (mouse.x - this.mouseDownPoint.mouseX) / this.worldRatio;
				var yDif = (mouse.y - this.mouseDownPoint.mouseY) / this.worldRatio;
				this.camera = {
					x: this.mouseDownPoint.cameraX + xDif, 
					y: this.mouseDownPoint.cameraY + yDif
				}
			}			
		} else {
			for (var obj of this.worldObjects.values()) {
				var objPoint = this.toCanvasPoint(obj.x, obj.y);
				var objSize = obj.size * this.worldRatio;
				obj.focused = this.cursorInRect(mouse.x, mouse.y, objPoint.x - objSize / 2, objPoint.y - objSize / 2, objSize, objSize);
			}
		}
		this.draw();
	}
	
	handleMouseUp() {
		this.isDraggable = false;
		for (var obj of this.worldObjects.values()) {
			if (obj.dragging == true) {
				if (typeof this.callbackViewDragged !== 'undefined') {
					this.callbackViewDragged(obj);
				}
			}
			obj.dragging = false;
		}
	}
	
	handleMouseWheel(event) {
		this.worldRatio -= event.deltaY / 2000.0;
		if (this.worldRatio < 0.05) this.worldRatio = 0.05;
		if (this.worldRatio > 2.0) this.worldRatio = 2.0;
		this.draw();
		return false; 
	}

}

class WebSocketManager {
	
	constructor(gameWorld, url, callback) {
		this.gameWorld = gameWorld;
		
		gameWorld.callbackViewDragged = obj => {
			this.sendMovementUpdate(obj.x, obj.y);
		};
		
		this.webSocket = new WebSocket(url);
		this.webSocket.onmessage = event => {
			this.handleMessage(event);
		};
	}
	
	sendMessage(msg) {
		console.log("SEND: " + msg);
		this.webSocket.send(msg);
	}

	sendMovementUpdate(x, y) {
		this.sendMessage("move;0;" + Math.round(x) + ";" + Math.round(y) + ";0;0");
	}
	
	handleMessage(event) {
		console.log("RECEIVE: " + event.data);
		const split = event.data.split(";");
		switch (split[0]) {
			case 'state':
				this.handleStateMessage(split);
				break;
			
		}
	}
	
	handleStateMessage(split) {
		var newObjects = new Map();
		for (let i = 2; i < split.length; i ++) {
			var object = new WorldObject(split[i]);
			newObjects.set(object.id, object);
			var oldObject = this.gameWorld.worldObjects.get(object.id);
			if (typeof oldObject !== 'undefined') {
				object.active = oldObject.active;
				object.focused = oldObject.focused;
				object.dragging = oldObject.dragging;
				if (oldObject.dragging) {
					// Keep dragging location
					object.x = oldObject.x;
					object.y = oldObject.y;
					object.angle = oldObject.angle;
				}
			} 
		}
		this.gameWorld.worldObjects = newObjects;
		this.gameWorld.draw();
	}
	
}

//wss://dl-websockets-25f48806cc22.herokuapp.com/websocket

var gameWorld = new GameWorld(document.getElementById("canvas"));
var webSocketManager = new WebSocketManager(gameWorld, "ws://localhost:8080/websocket");

window.addEventListener('resize', () => {
    gameWorld.updateSize(document.documentElement.clientWidth, document.documentElement.clientHeight);
    gameWorld.draw();
})
gameWorld.updateSize(document.documentElement.clientWidth, document.documentElement.clientHeight);
gameWorld.draw();