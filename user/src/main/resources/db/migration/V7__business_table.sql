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

drop table if exists `train`;
create table `train`
(
    `id`           bigint      not null comment 'id',
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
    unique key `code_unique` (`code`)
) comment='车次';


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


drop table if exists `train_carriage`;
create table `train_carriage`
(
    `id`           bigint      not null comment 'id',
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

    unique key `train_code_index_unique` (`train_code`, `index`),
    primary key (`id`)
) comment ='火车车厢';


drop table if exists `train_seat`;
create table `train_seat`
(
    `id`                  bigint      not null comment 'id',
    `train_code`          varchar(20) not null comment '车次编号｜searchable',
    `carriage_index`      int         not null comment '厢序',
    `row`                 char(2)     not null comment '排号｜01,02...',
    `seat_type`           char(1)     not null comment '座位类型｜【1-一等座、2-二等座】',
    `col`                 char(1)     not null comment '列号',-- 一等座(A/C/D/F)、二等座(A/B/C/D/F)】
    `carriage_seat_index` int         not null comment '同车厢座序',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`)
) comment ='车厢座位';