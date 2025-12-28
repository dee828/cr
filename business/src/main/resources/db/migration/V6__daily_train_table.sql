drop table if exists `daily_train`;
create table `daily_train`
(
    `id`           bigint      not null comment 'id',
    `date`         date        not null comment '日期',
    `code`         varchar(20) not null comment '车次编号｜searchable',
    `type`         char(1)     not null comment '车次类型｜【G-高铁、D-动车、K-快速】',
    `start`        varchar(20) not null comment '始发站｜searchable',
    `start_pinyin` varchar(50) not null comment '始发站拼音｜searchable',
    `start_time`   time        not null comment '出发时间',
    `end`          varchar(20) not null comment '终点站｜searchable',
    `end_pinyin`   varchar(50) not null comment '终点站拼音｜searchable',
    `end_time`     time        not null comment '到站时间',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`),
    unique key `date_code_unique` (`date`, `code`)
) comment='每日车次';