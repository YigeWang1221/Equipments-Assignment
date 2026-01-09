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
@ApiModel(value="Usr对象", description="")
public class Usr implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "普通用户id")
    @TableId(value = "usr_id", type = IdType.AUTO)
    private Integer usrId;

    @ApiModelProperty(value = "姓名")
    private String usrName;

    @ApiModelProperty(value = "card的卡号")
    private String usrCard;

    @ApiModelProperty(value = "密码")
    private String usrPassword;


}
