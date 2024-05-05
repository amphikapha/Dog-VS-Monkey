package component;

import javafx.scene.canvas.GraphicsContext;

/*
Renderable interface is used to create classes with the render method
 */
public interface Renderable {
    //Any class that implements the Renderable interface will need to provide an implementation for this method
    public abstract void render(GraphicsContext gc);
}
