DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    `id`         bigint       NOT NULL COMMENT 'ID',
    `email`      varchar(255) NOT NULL COMMENT '邮箱',
    `password`   varchar(255) DEFAULT NULL COMMENT '密码',
    `name`       varchar(255) DEFAULT NULL COMMENT '用户名',
    `mobile`     varchar(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`     varchar(512) DEFAULT NULL COMMENT '头像地址',
    `enabled`    tinyint(1)   DEFAULT 1 COMMENT '是否启用',
    `created_at` datetime     DEFAULT NULL COMMENT '创建时间',
    `updated_at` datetime     DEFAULT NULL COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_phone` (`mobile`)
) COMMENT ='用户';