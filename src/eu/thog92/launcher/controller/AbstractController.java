package eu.thog92.launcher.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import eu.thog92.launcher.model.AbstractModel;
import fr.wherecraft.launcher.view.IView;

public abstract class AbstractController implements PropertyChangeListener
{
    
    private ArrayList<IView> registeredViews;
    private ArrayList<AbstractModel> registeredModels;
    
    public AbstractController()
    {
        registeredViews = new ArrayList<IView>();
        registeredModels = new ArrayList<AbstractModel>();
    }
    
    public void addModel(AbstractModel model)
    {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }
    
    public void removeModel(AbstractModel model)
    {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }
    
    public void addView(IView view)
    {
        registeredViews.add(view);
    }
    
    public void removeView(IView view)
    {
        registeredViews.remove(view);
    }
    
    // Use this to observe property changes from registered models
    // and propagate them on to all the views.
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        for (IView view : registeredViews)
        {
            view.modelPropertyChange(evt);
        }
    }
    
    public void propertyChange(Object source, String propertyName, Object value)
    {
        this.propertyChange(new PropertyChangeEvent(source, propertyName, null,
                value));
    }
    
    /**
     * This is a convenience method that subclasses can call upon to fire
     * property changes back to the models. This method uses reflection to
     * inspect each of the model classes to determine whether it is the owner of
     * the property in question. If it isn't, a NoSuchMethodException is thrown,
     * which the method ignores.
     *
     * @param propertyName
     *            = The name of the property.
     * @param newValue
     *            = An object that represents the new value of the property.
     */
    protected void setModelProperty(String propertyName, Object newValue)
    {
            for (AbstractModel model : registeredModels)
            {
                
                Method method;
                try
                {
                    method = model.getClass().getMethod(propertyName,
                            new Class[]
                            { newValue.getClass() }
                    
                    );
                    method.invoke(model, newValue);
                } catch (NoSuchMethodException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                
                System.out.println("LOLOO");
            }
    }
    
}