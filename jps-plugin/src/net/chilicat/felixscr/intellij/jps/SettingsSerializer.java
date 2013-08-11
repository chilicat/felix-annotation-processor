package net.chilicat.felixscr.intellij.jps;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

public class SettingsSerializer extends JpsProjectExtensionSerializer {

    public SettingsSerializer() {
        super("felix-scr.xml", "ScrSettings");
    }

    @Override
    public void loadExtension(@NotNull JpsProject jpsProject, @NotNull Element element) {
        Settings.State state = XmlSerializer.deserialize(
                element, Settings.State.class);

        if (state == null) {
            state = new Settings.State();
        }
        JPSSCRExtensionService.getInstance().setSettings(jpsProject, new Settings(state));
    }

    @Override
    public void saveExtension(@NotNull JpsProject jpsProject, @NotNull Element element) {
        throw new UnsupportedOperationException("saveExtension is not supported.");
    }
}
