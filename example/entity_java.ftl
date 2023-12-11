package ${config.basepkg}.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ${entity.name}Entity {

// 实体属性
<#list entity.columns as column>
    private ${column.type} ${column.name};

</#list>
}