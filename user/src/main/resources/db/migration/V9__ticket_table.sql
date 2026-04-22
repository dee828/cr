drop table if exists `ticket`;
create table `ticket`
(
    `id`             bigint      not null comment 'id',
    `user_id`        bigint      not null comment '会员ID',
    `passenger_id`   bigint      not null comment '乘客ID',
    `passenger_name` varchar(20) comment '乘客姓名｜searchable',
    `train_date`     date        not null comment '日期',
    `train_code`     varchar(20) not null comment '车次编号',
    `carriage_index` int         not null comment '厢序',
    `seat_row`       char(2)     not null comment '排号｜01,02...',
    `seat_col`       char(1)     not null comment '列号',-- 一等座(A/C/D/F)、二等座(A/B/C/D/F)
    `start_station`  varchar(20) not null comment '出发站',
    `start_time`     time        not null comment '出发时间',
    `end_station`    varchar(20) not null comment '到达站',
    `end_time`       time        not null comment '到站时间',
    `seat_type`      char(1)     not null comment '座位类型｜【1-一等座、2-二等座】',

    -- 通用基础字段
    `created_at`     datetime default null comment '创建时间',
    `created_by`     bigint   default null comment '创建人ID',
    `updated_at`     datetime default null comment '更新时间',
    `updated_by`     bigint   default null comment '修改人ID',
    `deleted`        bigint   default 0 comment '逻辑删除标记',
    `deleted_at`     datetime default null comment '删除时间',
    `deleted_by`     bigint   default null comment '删除人ID',

    primary key (`id`),
    index `user_id_index` (`user_id`)
) comment ='车票记录';