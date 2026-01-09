package com.example.objectlibdb.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("libUse")
@ApiModel(value="LibUse对象", description="")
public class LibUse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "实验室进出id")
    @TableId(value = "libUse_id", type = IdType.AUTO)
    private Integer libuseId;

    @ApiModelProperty(value = "实验室id")
    private Integer libId;

    @ApiModelProperty(value = "实验室状态 -0 无人 -1 有人")
    private Integer libStatus;

    @ApiModelProperty(value = "实验室进入时间 年月日小时分钟")
    @TableField("lib_inTime")
    private String libIntime;

    @ApiModelProperty(value = "实验室离开时间 年月日小时分钟")
    @TableField("lib_outTime")
    private String libOuttime;

    @ApiModelProperty(value = "使用的人id")
    private Integer usrId;

    @ApiModelProperty(value = "使用的人的姓名")
    private String usrName;


}
