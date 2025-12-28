drop table if exists `train_station`;
create table `train_station`
(
    `id`          bigint        not null comment 'id',
    `train_code`  varchar(20)   not null comment '车次编号｜searchable',
    `index`       int           not null comment '站序｜约定第一站是0',
    `name`        varchar(20)   not null comment '站名',
    `name_pinyin` varchar(50)   not null comment '站名拼音',
    `in_time`     time comment '进站时间',
    `out_time`    time comment '出站时间',
    `stop_time`   time comment '停站时长',
    `km`          decimal(8, 2) not null comment '里程 km｜从上一站到本站的距离',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`),
    unique key `train_code_index_unique` (`train_code`, `index`),
    unique key `train_code_name_unique` (`train_code`, `name`)
) comment ='火车车站';