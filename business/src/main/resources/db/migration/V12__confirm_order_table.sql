drop table if exists `confirm_order`;
create table `confirm_order`
(
    `id`                    bigint      not null comment 'id',
    `user_id`               bigint      not null comment '会员ID',
    `date`                  date        not null comment '日期',
    `train_code`            varchar(20) not null comment '车次编号｜searchable',
    `start`                 varchar(20) not null comment '出发站｜searchable',
    `end`                   varchar(20) not null comment '到达站｜searchable',
    `daily_train_ticket_id` bigint      not null comment '余票ID',
    `tickets`               json        not null comment '车票',
    `status`                char(1)     not null comment '订单状态｜【I-初始、P-处理中、S-成功、F-失败、E-无票、C-取消】',

    -- 通用基础字段
    `created_at`            datetime default null comment '创建时间',
    `created_by`            bigint   default null comment '创建人ID',
    `updated_at`            datetime default null comment '更新时间',
    `updated_by`            bigint   default null comment '修改人ID',
    `deleted`               bigint   default 0 comment '逻辑删除标记',
    `deleted_at`            datetime default null comment '删除时间',
    `deleted_by`            bigint   default null comment '删除人ID',
    primary key (`id`),
    index `date_train_code_index` (`date`, `train_code`)
) comment ='确认订单';