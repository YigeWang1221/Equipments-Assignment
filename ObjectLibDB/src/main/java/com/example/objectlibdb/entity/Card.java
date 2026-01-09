package com.example.objectlibdb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@TableName("Card")
@ApiModel(value="Card对象", description="")
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "实验室进出id")
    @TableId(value = "cardId", type = IdType.AUTO)
    private Integer cardId;

    @ApiModelProperty(value = "ic卡的输入信息")
    @TableField("cardInfo")
    private String cardInfo;

    @ApiModelProperty(value = "ic卡的状态 -0 无人 -1 有人")
    private Integer status;


}
