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
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class CommandLine extends Task {
    private final List<Param> params = new ArrayList<>();

    public List<Param> getParams() {
        return params;
    }

    public void addParam(Param param) {
        params.add(param);
    }

    @Override
    public String toString() {
        return "CommandLine{" +
                "params=" + params +
                '}';
    }

    @Override
    public void execute() throws BuildException {
        if (params.isEmpty()) {
            throw new BuildException("Empty command line tag");
        }

        params.forEach(Param::execute);
    }

    public JsonValue toJson() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        params.forEach(param -> arrayBuilder.add(param.getValue()));

        return arrayBuilder.build();
    }
}
