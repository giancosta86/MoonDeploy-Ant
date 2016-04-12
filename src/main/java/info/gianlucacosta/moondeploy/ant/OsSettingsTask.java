/*§
  ===========================================================================
  MoonDeploy - Ant
  ===========================================================================
  Copyright (C) 2016 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.moondeploy.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

public abstract class OsSettingsTask extends Task {
    private List<Package> packages = new ArrayList<>();
    private CommandLine commandLine;
    private String iconPath;

    public List<Package> getPackages() {
        return packages;
    }

    public void addPkg(Package packageToAdd) {
        packages.add(packageToAdd);
    }


    public CommandLine getCommandLine() {
        return commandLine;
    }

    public void addCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public void execute() throws BuildException {
        packages.forEach(Package::execute);

        if (commandLine != null) {
            commandLine.execute();
        }
    }

    public void writeJsonOsSettings(JsonObjectBuilder jsonBuilder) {
        if (!packages.isEmpty()) {
            JsonObjectBuilder packagesBuilder = Json.createObjectBuilder();

            packages.forEach(pkg -> {
                String packageName = pkg.getName();

                String packageVersion = pkg.version != null && !pkg.version.isEmpty() ?
                        pkg.version
                        :
                        "";

                packagesBuilder.add(packageName, packageVersion);
            });

            jsonBuilder.add("Packages", packagesBuilder);
        }


        if (commandLine != null) {
            jsonBuilder.add("CommandLine", commandLine.toJson());
        }

        if (iconPath != null && !iconPath.isEmpty()) {
            jsonBuilder.add("IconPath", iconPath);
        }
    }
}
