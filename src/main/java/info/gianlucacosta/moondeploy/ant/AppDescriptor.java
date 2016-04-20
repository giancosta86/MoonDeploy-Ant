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

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppDescriptor extends OsSettingsTask {
    private static final String DefaultDescriptorFileName = "App.moondeploy";

    private String outputDir;
    private boolean verbose;

    private String declaredBaseURL;
    private String descriptorFileName;

    private String name;
    private String appVersion;
    private String publisher;
    private String description;

    private int skipPackageLevels;
    private boolean skipUpdateCheck;

    private List<SupportedOS> supportedOperatingSystems = new ArrayList<>();

    private List<OS> operatingSystems = new ArrayList<>();


    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setBaseURL(String declaredBaseURL) {
        this.declaredBaseURL = declaredBaseURL;
    }

    public void setDescriptorFileName(String descriptorFileName) {
        this.descriptorFileName = descriptorFileName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.appVersion = version;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkipPackageLevels(int skipPackageLevels) {
        this.skipPackageLevels = skipPackageLevels;
    }

    public void setSkipUpdateCheck(boolean skipUpdateCheck) {
        this.skipUpdateCheck = skipUpdateCheck;
    }


    public void addSupportedOS(SupportedOS supportedOS) {
        supportedOperatingSystems.add(supportedOS);
    }


    public void addOS(OS os) {
        operatingSystems.add(os);
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (verbose) {
            System.err.printf("The in-memory descriptor structure is: %s\n", this);
        }

        validate();

        JsonObject jsonDescriptor = toJson();

        saveJsonDescriptor(jsonDescriptor);
    }

    public void validate() throws BuildException {
        if (declaredBaseURL == null || declaredBaseURL.isEmpty()) {
            throw new BuildException("Missing baseURL attribute");
        }

        if (name == null || name.isEmpty()) {
            throw new BuildException("Missing name attribute");
        }


        if (appVersion == null || appVersion.isEmpty()) {
            throw new BuildException("Missing appVersion attribute");
        }

        if (publisher == null || publisher.isEmpty()) {
            throw new BuildException("Missing publisher attribute");
        }

        if (description == null || description.isEmpty()) {
            throw new BuildException("Missing description attribute");
        }

        if (skipPackageLevels < 0) {
            throw new BuildException("skipPackageLevels must be >= 0");
        }

        supportedOperatingSystems.forEach(SupportedOS::execute);
        operatingSystems.forEach(OS::execute);
    }

    private JsonObject toJson() {
        JsonObjectBuilder descriptorBuilder = Json.createObjectBuilder()
                .add("DescriptorVersion", "3.0")
                .add("BaseURL", declaredBaseURL);

        if (descriptorFileName != null && !descriptorFileName.isEmpty()) {
            descriptorBuilder.add("DescriptorFileName", descriptorFileName);
        }

        descriptorBuilder = descriptorBuilder.add("Name", name)
                .add("Version", appVersion)
                .add("Publisher", publisher)
                .add("Description", description)

                .add("SkipPackageLevels", skipPackageLevels)
                .add("SkipUpdateCheck", skipUpdateCheck);


        JsonArray supportedOperatingSystems = getSupportedOperatingSystems();

        if (!supportedOperatingSystems.isEmpty()) {
            descriptorBuilder = descriptorBuilder.add("SupportedOS", supportedOperatingSystems);
        }


        writeJsonOsSettings(descriptorBuilder);


        descriptorBuilder = descriptorBuilder.add("OS", getOperatingSystemsBuilder());

        return descriptorBuilder.build();
    }


    private JsonArray getSupportedOperatingSystems() {
        JsonArrayBuilder resultBuilder =
                Json.createArrayBuilder();

        supportedOperatingSystems.forEach(supportedOs -> resultBuilder.add(supportedOs.getName()));

        return resultBuilder.build();
    }


    private JsonObjectBuilder getOperatingSystemsBuilder() {
        JsonObjectBuilder operatingSystemsBuilder = Json.createObjectBuilder();

        operatingSystems.forEach(specificOs -> {
            JsonObjectBuilder specificOsBuilder = Json.createObjectBuilder();
            specificOs.writeJsonOsSettings(specificOsBuilder);

            operatingSystemsBuilder.add(specificOs.getName(), specificOsBuilder);
        });

        return operatingSystemsBuilder;
    }


    private void saveJsonDescriptor(JsonObject jsonDescriptor) throws BuildException {
        File baseDir = getProject().getBaseDir();
        File targetDir;

        if (outputDir != null && !outputDir.isEmpty()) {
            targetDir = new File(baseDir, outputDir);
        } else {
            targetDir = new File(baseDir, "build");
        }

        targetDir.mkdirs();

        if (!targetDir.isDirectory()) {
            throw new BuildException("Cannot create target dir: " + targetDir);
        }


        File targetFile = descriptorFileName != null && !descriptorFileName.isEmpty() ?
                new File(targetDir, descriptorFileName)
                :
                new File(targetDir, DefaultDescriptorFileName);


        if (verbose) {
            System.err.printf("Saving to: '%s'\n", targetFile);
        }

        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            Map<String, Object> writerProperties = new HashMap<>();
            writerProperties.put(JsonGenerator.PRETTY_PRINTING, true);

            JsonWriterFactory writerFactory = Json.createWriterFactory(writerProperties);

            try (JsonWriter jsonWriter = writerFactory.createWriter(fileWriter)) {
                jsonWriter.writeObject(jsonDescriptor);
            }
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    @Override
    public String toString() {
        return "AppDescriptor{" +
                "outputDir='" + outputDir + '\'' +
                ", declaredBaseURL='" + declaredBaseURL + '\'' +
                ", descriptorFileName='" + descriptorFileName + '\'' +
                ", name='" + name + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", publisher='" + publisher + '\'' +
                ", description='" + description + '\'' +
                ", skipPackageLevels=" + skipPackageLevels +
                ", skipUpdateCheck=" + skipUpdateCheck +
                ", supportedOperatingSystems=" + supportedOperatingSystems +
                ", packages=" + getPackages() +
                ", commandLine=" + getCommandLine() +
                ", iconPath='" + getIconPath() + '\'' +
                ", operatingSystems=" + operatingSystems +
                '}';
    }
}
