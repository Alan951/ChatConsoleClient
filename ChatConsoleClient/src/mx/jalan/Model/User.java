package mx.jalan.Model;

import javax.websocket.Session;

public class User {
    private String nombre;
    private Session session;

    public User(){}
    
    public User(String nombre) {
		super();
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Session getSession(){
		return session;
	}

	public void setSession(Session session){
		this.session = session;
	}
    
    
}