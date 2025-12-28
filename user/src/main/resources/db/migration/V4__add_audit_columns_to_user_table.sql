alter table `user` add column `created_by` bigint default 0 comment '创建人ID' after `created_at`;

alter table `user` add column `updated_by` bigint default 0 comment '更新人ID' after `updated_at`;