# ChatConsoleClient
--------------------
ChatConsoleClient es un chat que utiliza la tecnología WebSockets y es parte de mi otro proyect 
ChatConsoleClient fue desarrollado en Eclipse y para importarlo debes de hacer lo siguiente

### Importar proyecto
---------------------
Toolbar -> File -> Import -> Projects from Git -> Clone URI
URI: https://github.com/Alan951/ChatConsoleClient
Next -> Next -> Next -> Import existing Eclipse projects -> Finish

### Importante
----------------
En mx.jalan.App/App existe una variable String llamada URL, en ella contiene la URL del websocket, este url es la dirección del websocket.

### Comandos
-------------
```
IMPORTANTE: Solo es posible ingresar un comando a la vez y estos van seguidos de una diagonal. Ej: \conn Jorge
[Comando] - [Descripción]
/conn <nombre usuario> ó /connect <nombre usuario> - Sirve para conectarse al websocket. Es necesario especificar el nombre de usuario.
/online - Ver todos los usuarios conectados en el chat.
/dis ó /disconnect - Sirve para desconectarse del websocket.
/clear - Limpia la terminal.
/pm <nombre usuario> - Envia un mensaje privado a un usuario en especifico.
/help - Ver lista de todos los comandos.
/exit - Salir del programa.
```
