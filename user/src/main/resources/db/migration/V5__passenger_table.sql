-- 乘车人/乘客
drop table if exists `passenger`;
create table `passenger`
(
    `id`         bigint      not null comment 'id',
    `user_id`    bigint      not null comment '会员id',
    `name`       varchar(18) not null comment '姓名｜searchable',
    `id_card`    varchar(18) not null comment '身份证号',
    `mobile`     varchar(11) not null comment '手机号｜searchable',
    `type`       char(1)     not null comment '乘客类型｜【1-成人、2-儿童、3-学生】',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`),
    key `idx_user_id` (`user_id`),
    unique key `uk_user_id_id_card` (`user_id`, `id_card`, `deleted`)
) engine = innodb
  default charset = utf8mb4 comment ='乘车人/乘客';