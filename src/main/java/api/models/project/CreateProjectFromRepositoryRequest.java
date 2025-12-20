package api.models.project;

import api.generators.GeneratingRule;
import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectFromRepositoryRequest extends BaseModel {

    private ParentProject parentProject;
    @GeneratingRule(regex = "^[^#\\^<!}{(\"`.[] ]{1,80}$")
    private String name;
    @GeneratingRule(regex = "^[A-Za-z][A-Za-z0-9_]{0,224}$")
    private String id;
    private List<VcsRootEntry> vcsRootEntries;
}