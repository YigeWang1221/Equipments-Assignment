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
@TableName("libInfo")
@ApiModel(value="LibInfo对象", description="")
public class LibInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "实验室信息id")
    @TableId(value = "libInfo_id", type = IdType.AUTO)
    private Integer libinfoId;

    @ApiModelProperty(value = "实验室id")
    private String libId;

    @ApiModelProperty(value = "实验室温度")
    private String libTemp;

    @ApiModelProperty(value = "实验室湿度")
    private String libHum;

    @ApiModelProperty(value = "实验室pm2.5")
    @TableField("Lib_PM")
    private String libPm;

    @ApiModelProperty(value = "记录的时间")
    private String getTime;


}
