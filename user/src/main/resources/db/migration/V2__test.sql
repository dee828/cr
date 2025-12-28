DROP TABLE IF EXISTS `test`;

CREATE TABLE `test`
(
    `id`         bigint       NOT NULL COMMENT 'ID',
    `email`      varchar(255) NOT NULL COMMENT '邮箱',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`)
) COMMENT ='测试';