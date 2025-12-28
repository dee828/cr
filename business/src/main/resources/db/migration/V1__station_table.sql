drop table if exists `station`;
create table `station`
(
    `id`          bigint      not null comment 'id',
    `name`        varchar(20) not null comment '站名｜searchable',
    `name_pinyin` varchar(50) not null comment '站名拼音｜searchable',
    `name_py`     varchar(50) not null comment '拼音首字母｜searchable',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`),
    unique key `name_unique` (`name`)
) comment ='车站';