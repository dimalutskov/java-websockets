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
		this.health = parseInt(split[6]);
		this.energy = parseInt(split[7]);
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
		this.callbackViewSelected;
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
		var selectedEntity = null;
		for (var obj of this.worldObjects.values()) {
		    if (obj.active == true) {
		        selectedEntity = obj;
		    }
			if (obj.dragging == true) {
				if (typeof this.callbackViewDragged !== 'undefined') {
					this.callbackViewDragged(obj);
				}
			}
			obj.dragging = false;
		}
		if (typeof this.callbackViewSelected !== 'undefined') {
            this.callbackViewSelected(selectedEntity);
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

class EditEntityContainer {

    constructor(gameWorld, container) {
        this.gameWorld = gameWorld;
        this.container = document.getElementById("entityEditContainer");

        // Inputs
        this.inputSize = document.getElementById("editSize");
        this.inputX = document.getElementById("editX");
        this.inputY = document.getElementById("editY");
        this.inputAngle = document.getElementById("editAngle");
        this.inputHealth = document.getElementById("editHealth");
        this.inputEnergy = document.getElementById("editEnergy");

        const inputHandler = e => {
            this.update();
        }
        this.inputSize.addEventListener('input', inputHandler);
        this.inputX.addEventListener('input', inputHandler);
        this.inputY.addEventListener('input', inputHandler);
        this.inputAngle.addEventListener('input', inputHandler);
        this.inputHealth.addEventListener('input', inputHandler);
        this.inputEnergy.addEventListener('input', inputHandler);

        // Buttons
        this.buttonSave = document.getElementById("buttonEditSave");
        this.buttonAdd = document.getElementById("buttonAdd");

        this.selectedEntity = null; // TODO Subscribe destroyed

        this.hide();

        this.gameWorld.callbackViewSelected = entity => {
            this.setSelectedEntity(entity);
        }
    }

    isHidden() {
        return this.container.style.display === 'none'
    }

    show(selectedEntity) {
        this.container.hidden = false;
        this.container.style.display = '';
        this.selectedEntity = selectedEntity;
        this.update();
        this.buttonAdd.innerHTML = "Cancel"
    }

    hide() {
        this.container.style.display = 'none';
        this.selectedEntity = null;
        this.buttonAdd.innerHTML = "Add Entity"
    }

    update() {
        var editMode = this.selectedEntity != null;
        document.getElementById("buttonEditDestroy").style.display = editMode ? '' : 'hidden';
        document.getElementById("entityEditSkills").style.display = editMode ? '' : 'none';
        document.getElementById("entityEditTypeSelector").disabled = editMode;
        this.buttonSave.disabled = !this.isInputValid();
        if (this.selectedEntity != null) {
            this.setInput(this.inputSize, this.selectedEntity.size);
            this.setInput(this.inputX, this.selectedEntity.x);
            this.setInput(this.inputY, this.selectedEntity.y);
            this.setInput(this.inputAngle, this.selectedEntity.angle);
            this.setInput(this.inputHealth, this.selectedEntity.health);
            this.setInput(this.inputEnergy, this.selectedEntity.energy);
        }
    }

    setInput(inputElement, value) {
        if (document.activeElement != inputElement) inputElement.value = value;
    }

    isInputValid() {
        return this.inputSize.value.length > 0 && this.inputX.value.length > 0 &&
            this.inputY.value.length > 0 && this.inputAngle.value.length > 0 &&
            this.inputHealth.value.length > 0 && this.inputEnergy.value.length > 0
    }

    setSelectedEntity(entity) {
        if (entity == null) {
            if (this.selectedEntity != null) this.hide();
        } else {
            if (this.selectedEntity == null || this.selectedEntity.id != entity.id) {
                this.show(entity);
            }
        }
    }
}

class WebSocketManager {
	
	constructor(gameWorld, url, updateCallback) {
		this.gameWorld = gameWorld;
		this.url = url;
		this.updateCallback = updateCallback;
		this.isConnected = false;
		this.isJoin = false;
	}

	connect() {
	    this.webSocket = new WebSocket(this.url);
        this.webSocket.onopen = event => {
            this.isConnected = true;
            this.updateCallback();
        };
        this.webSocket.onerror = event => {
           this.updateCallback();
        };
        this.webSocket.onclose = event => {
            this.isConnected = false;
            this.isJoin = false;
            this.updateCallback();
        };
        this.webSocket.onmessage = event => {
        	this.handleMessage(event);
        };
	}

	disconnect() {
	    this.webSocket.close();
	}
	
	sendMessage(msg) {
		console.log("SEND: " + msg);
		this.webSocket.send(msg);
	}

	handleMessage(event) {
		console.log("RECEIVE: " + event.data);
		const split = event.data.split(";");
		switch (split[0]) {
		    case 'join':
		        this.isJoin = true;
		        this.updateCallback();
		        break;
		    case 'leave':
            	this.isJoin = false;
            	this.updateCallback();
            	break;
			case 'state':
				this.handleStateMessage(split);
				break;
			
		}
	}
	
	handleStateMessage(split) {
		var newObjects = new Map();
		for (let i = 2; i < split.length; i ++) {
			var object = new WorldObject(split[i]);
			var oldObject = this.gameWorld.worldObjects.get(object.id);
			if (typeof oldObject !== 'undefined') {
			    if (oldObject.dragging == false) {
			        oldObject.x = object.x;
                    oldObject.y = object.y;
                }
                oldObject.angle = object.angle;
                oldObject.size = object.size;
                oldObject.health = object.health;
                oldObject.energy = object.energy;

                newObjects.set(object.id, oldObject);
			} else {
			    newObjects.set(object.id, object);
			}
		}
		this.gameWorld.worldObjects = newObjects;
		this.gameWorld.draw();
	}
	
}

//

//var webSocketUrl = "ws://localhost:8080/websocket";
//var serverUrl = "http://localhost:8080";

var webSocketUrl = "wss://dl-websockets-25f48806cc22.herokuapp.com/websocket";
var serverUrl = "https://dl-websockets-25f48806cc22.herokuapp.com";

var gameWorld = new GameWorld(document.getElementById("canvas"));
var editEntityContainer = new EditEntityContainer(gameWorld);
var webSocketManager = new WebSocketManager(gameWorld, webSocketUrl, () => {
    var connectButton = document.getElementById("buttonConnect");
    var joinButton = document.getElementById("buttonJoin");
    if (webSocketManager.isConnected == true) {
        connectButton.disabled = false;
        connectButton.innerHTML = "Disconnect"
        joinButton.disabled = false;
        joinButton.innerHTML = webSocketManager.isJoin == true ? "Leave" : "Join";
        document.getElementById("buttonAdd").disabled = false;
    } else {
        connectButton.disabled = false;
        connectButton.innerHTML = "Connect";
        joinButton.disabled = true;
        joinButton.innerHTML = "Join";
        document.getElementById("buttonAdd").disabled = true;
    }
});

var SKILL_TYPE_SHOT = 1;

gameWorld.callbackViewDragged = obj => {
    fetch(serverUrl + "/entity/" + obj.id, {
          method: "POST",
          body: JSON.stringify({
            x: obj.x,
            y: obj.y,
            angle: obj.angle
          }),
          headers: {
            "Content-type": "application/json; charset=UTF-8"
          }
    });
};

window.addEventListener('resize', () => {
    gameWorld.updateSize(document.documentElement.clientWidth, document.documentElement.clientHeight);
    gameWorld.draw();
})
gameWorld.updateSize(document.documentElement.clientWidth, document.documentElement.clientHeight);
gameWorld.draw();

function clickConnect() {
    if (webSocketManager.isConnected == true) {
        webSocketManager.disconnect();
        editEntityContainer.hide();
    } else {
        webSocketManager.connect();
        document.getElementById("buttonConnect").disabled = true;
    }
}

function clickJoin() {
    document.getElementById("buttonJoin").disabled = true;
    if (webSocketManager.isJoin == true) {
        webSocketManager.sendMessage("leave");
    } else {
        webSocketManager.sendMessage("join");
    }
}

function clickAddEntity() {
    if (editEntityContainer.isHidden()) {
        editEntityContainer.show();
    } else {
        editEntityContainer.hide();
    }
}

function clickEditSave() {
    var url = serverUrl + "/entity";
    if (editEntityContainer.selectedEntity != null) {
        url += "/" + editEntityContainer.selectedEntity.id
    }
    fetch(url, {
        method: "POST",
        body: JSON.stringify({
          size: Number(editEntityContainer.inputSize.value),
          x: Number(editEntityContainer.inputX.value),
          y: Number(editEntityContainer.inputY.value),
          angle: Number(editEntityContainer.inputAngle.value),
          health: Number(editEntityContainer.inputHealth.value),
          energy: Number(editEntityContainer.inputEnergy.value)
        }),
        headers: {
          "Content-type": "application/json; charset=UTF-8"
        }
    });
    editEntityContainer.hide();
}

function clickEditCancel() {
    editEntityContainer.hide(); // TODO deselect object
    document.getElementById("buttonAdd").innerHTML = "Add Entity"
}

function clickEditDestroy() {
    if (editEntityContainer.selectedEntity != null) {
        fetch(serverUrl + "/entity/" + editEntityContainer.selectedEntity.id, {
              method: "DELETE",
              body: "",
              headers: {
                "Content-type": "application/json; charset=UTF-8"
              }
            });
    }
}

function clickSkillShot() {
    if (editEntityContainer.selectedEntity != null) {
        fetch(serverUrl + "/skill", {
              method: "POST",
              body: JSON.stringify({
                id: editEntityContainer.selectedEntity.id,
                skillId: SKILL_TYPE_SHOT
              }),
              headers: {
                "Content-type": "application/json; charset=UTF-8"
              }
            });
    }
}