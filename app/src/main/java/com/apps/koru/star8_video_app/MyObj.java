package com.apps.koru.star8_video_app;

public class MyObj
{
    public MyObj(boolean value)
    {
        setValue(value);
    }

    private boolean myValue;
    public boolean getValue() { return myValue; }
    public void setValue( boolean value )
    {
        if (value != myValue)
        {
            myValue = value;
            signalChanged();
        }
    }

    public interface VariableChangeListener
    {
        public void onVariableChanged(Object... variableThatHasChanged);
    }

    private VariableChangeListener variableChangeListener;
    public void setVariableChangeListener(VariableChangeListener variableChangeListener)
    {
        this.variableChangeListener = variableChangeListener;
    }

    private void signalChanged()
    {
        if (variableChangeListener != null)
            variableChangeListener.onVariableChanged(myValue);
    }
}