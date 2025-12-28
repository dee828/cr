-- 余票信息
drop table if exists `daily_train_ticket`;
create table `daily_train_ticket`
(
    `id`           bigint        not null comment 'id',
    `date`         date          not null comment '日期',
    `train_code`   varchar(20)   not null comment '车次编号｜searchable',
    `start`        varchar(20)   not null comment '出发站｜searchable',
    `start_pinyin` varchar(50)   not null comment '出发站拼音｜searchable',
    `start_time`   time          not null comment '出发时间',
    `start_index`  int           not null comment '出发站序｜本站是整个车次的第几站',
    `end`          varchar(20)   not null comment '到达站｜searchable',
    `end_pinyin`   varchar(50)   not null comment '到达站拼音｜searchable',
    `end_time`     time          not null comment '到站时间',
    `end_index`    int           not null comment '到站站序｜本站是整个车次的第几站',
    `ydz`          int           not null comment '一等座余票',
    `ydz_price`    decimal(8, 2) not null comment '一等座票价',
    `edz`          int           not null comment '二等座余票',
    `edz_price`    decimal(8, 2) not null comment '二等座票价',

    -- 通用基础字段
    `created_at`   datetime default null comment '创建时间',
    `created_by`   bigint   default null comment '创建人ID',
    `updated_at`   datetime default null comment '更新时间',
    `updated_by`   bigint   default null comment '修改人ID',
    `deleted`      bigint   default 0 comment '逻辑删除标记',
    `deleted_at`   datetime default null comment '删除时间',
    `deleted_by`   bigint   default null comment '删除人ID',

    primary key (`id`),
    unique key `date_train_code_start_end_unique` (`date`, `train_code`, `start`, `end`)
) comment ='余票信息';