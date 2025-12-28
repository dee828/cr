drop table if exists `daily_train_seat`;
create table `daily_train_seat`
(
    `id`                  bigint      not null comment 'id',
    `date`         date        not null comment '日期',
    `train_code`          varchar(20) not null comment '车次编号｜searchable',
    `carriage_index`      int         not null comment '厢序',
    `row`                 char(2)     not null comment '排号｜01,02...',
    `seat_type`           char(1)     not null comment '座位类型｜【1-一等座、2-二等座】',
    `col`                 char(1)     not null comment '列号',-- 一等座(A/C/D/F)、二等座(A/B/C/D/F)】
    `carriage_seat_index` int         not null comment '同车厢座序',
    `sell`                varchar(90) not null comment '售卖情况｜用0/1代表车站区间的售卖情况，0表示可卖，1表示已卖',

    -- 通用基础字段
    `created_at` datetime default null comment '创建时间',
    `created_by` bigint   default null comment '创建人ID',
    `updated_at` datetime default null comment '更新时间',
    `updated_by` bigint   default null comment '修改人ID',
    `deleted`    bigint   default 0 comment '逻辑删除标记',
    `deleted_at` datetime default null comment '删除时间',
    `deleted_by` bigint   default null comment '删除人ID',

    primary key (`id`),
    unique key `date_train_code_carriage_index_unique` (`date`, `train_code`, `carriage_index`, `carriage_seat_index`)
) comment ='每日车厢座位';