package com.example.objectlibdb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author KLE1NBLUE&LackOxy
 * @since 2023-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Lib对象", description="")
public class Lib implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "实验室id")
    @TableId(value = "lib_id", type = IdType.AUTO)
    private Integer libId;

    @ApiModelProperty(value = "实验室名称")
    private String libName;


}
