drop table if exists `daily_train_carriage`;
create table `daily_train_carriage`
(
    `id`           bigint      not null comment 'id',
    `date`         date        not null comment '日期',
    `train_code`   varchar(20) not null comment '车次编号｜searchable',
    `index`        int         not null comment '厢号',
    `seat_type`    char(1)     not null comment '座位类型｜【1-一等座、2-二等座】',
    `seat_count`   int         not null comment '座位数',
    `row_count`    int         not null comment '排数',
    `col_count` int         not null comment '列数',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`),
    unique key `date_train_code_index_unique` (`date`, `train_code`, `index`)
) comment ='每日火车车厢';