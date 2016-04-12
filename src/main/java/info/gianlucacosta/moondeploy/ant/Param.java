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

public class Param extends Task {
    private String value;

    public String getValue() {
        return value;
    }

    public void addText(String value) {
        this.value = value;
    }

    @Override
    public void execute() throws BuildException {
        if (value == null || value.isEmpty()) {
            throw new BuildException("Empty param value");
        }
    }

    @Override
    public String toString() {
        return "Param{" +
                "value='" + value + '\'' +
                '}';
    }
}
