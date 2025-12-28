drop table if exists `sb`;
create table `sb`
(
    `id`          bigint                  not null comment 'id',
    `name`        varchar(20)             not null comment '名称｜searchable',
    `level`       char(1)                 not null comment '傻逼登记｜【1-新手、2-青铜、3-王者】',
    `code`        int                     not null comment '证书编号',
    `start_date`  date                    not null comment '颁发日期',
    `start_time`  time                    not null comment '颁发时间',
    `end_time`    datetime      default null comment '过期时间',
    `proofread`   tinyint       default 1 null COMMENT '实名认证',
    `kg`          decimal(8, 2) default null comment '体重',
    `description` varchar(512)  default null comment '描述',
    `content`     text          default null comment '简介',
    `avatar`      varchar(255)  default null comment '头像｜imageColumn',

    -- 通用基础字段
    `created_at`  datetime      default null comment '创建时间',
    `created_by`  bigint        default null comment '创建人ID',
    `updated_at`  datetime      default null comment '更新时间',
    `updated_by`  bigint        default null comment '修改人ID',
    `deleted`     bigint        default 0 comment '逻辑删除标记',
    `deleted_at`  datetime      default null comment '删除时间',
    `deleted_by`  bigint        default null comment '删除人ID',

    primary key (`id`),
    unique key `name_unique` (`name`)
) comment ='傻逼';

-- hw
-- 分析之前那 5 张表的关系、映射到每个人自己想做的系统，可以从已经完成的代码获得灵感，然后在假期全力完成自己想做的系统（至少完成后端）
--   通用化、模版化、其实就是思路化了
--   慢慢体会我的用意
--   待完善：
--     给普通用户访问的前端，差异化比较大，不建议用模板，自己发挥，但是安装分步迭代的思路，至少后台管理可以很快完成了
--     文件上传（可以用完整手动填写url来替代）
--     富文本编辑器，如文章内容等（暂时可以只支持纯文本）
--     ...