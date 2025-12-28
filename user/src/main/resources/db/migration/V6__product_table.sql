DROP TABLE IF EXISTS `product`;

CREATE TABLE `product`
(
    `id`         bigint       NOT NULL COMMENT 'ID',
    `slug`      varchar(255) NOT NULL COMMENT 'slug',
    `name`      varchar(255) NOT NULL COMMENT '名称',
    `description`      varchar(255) NOT NULL COMMENT '描述',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) COMMENT ='产品';