package pp.spirit.io.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import pp.spirit.base.base.BaseBean;
import pp.spirit.base.incrementer.IdGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.dao.datamodel
 * @Description: 文件实体类
 * @date 2021.2.3  11:26
 * @email 453826286@qq.com
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@TableName("pp_file")
@Table(name="pp_file")
@ApiModel(value="PPFile", description="文件实体类")
public class PPFile extends BaseBean<PPFile> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "文件编号")
    private String fileId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件MimeType")
    private String mimeType;

    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "下载次数")
    private Integer downloadTimes = 0;

    @ApiModelProperty(value = "所属应用")
    private String app = "default";

    @ApiModelProperty(value = "所属模块")
    private String model = "default";

    @ApiModelProperty(value = "上传时间")
    private Date uploadTime ;

    @ApiModelProperty(value = "上传人")
    private String uploader = "default";

    @ApiModelProperty(value = "文件")
    @TableField(exist = false)
    private transient byte[] fileData;
}
