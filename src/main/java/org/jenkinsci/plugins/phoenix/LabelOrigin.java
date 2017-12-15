package org.jenkinsci.plugins.phoenix;

import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class LabelOrigin
{
    @Exported
    private Label label;

    public LabelOrigin(Label label)
    {
        this.label = label;
    }

    @Exported
    public Label getLabel()
    {
        return label;
    }

    public void setLabel(Label label)
    {
        this.label = label;
    }

    @Exported
    public String getName()
    {
        return this.label.getName();
    }

    @Exported
    public String getDescription()
    {
        return this.label.getDescription();
    }
}