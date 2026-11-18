package com.kerp.modules.integration.service.impl;

import com.kerp.modules.integration.dto.IntegrationOverviewResponse;
import com.kerp.modules.integration.dto.IntegrationProviderItem;
import com.kerp.modules.integration.service.IntegrationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Override
    public IntegrationOverviewResponse getOverview() {
        return new IntegrationOverviewResponse(
                List.of(
                        "当前版本以本机可运行为第一优先，不依赖任何第三方平台账号。",
                        "业务模块只产生通知、审批、库存等领域事件，外部平台只作为通道或同步增强层。",
                        "身份、组织、通知、文件能力都通过统一接口预留，后续接入时不重做现有业务模型。"
                ),
                List.of(
                        "邮件服务账号",
                        "企业微信开放平台应用",
                        "飞书开放平台应用",
                        "钉钉开放平台应用",
                        "短信网关账号",
                        "域名与 HTTPS"
                ),
                List.of(
                        new IntegrationProviderItem(
                                "IN_APP",
                                "站内通知",
                                "通知",
                                "ENABLED",
                                "当前唯一启用的内置通知通道，用于审批、流程、资产和库存事件提醒。",
                                List.of("站内消息发送", "按用户/角色/部门投递", "通知中心查询"),
                                List.of("审批节点提醒增强", "库存预警联动", "人事异动广播"),
                                List.of()
                        ),
                        new IntegrationProviderItem(
                                "SMTP",
                                "SMTP 邮件",
                                "通知",
                                "RESERVED",
                                "保留为正式邮件通知通道，未来可承接审批提醒、库存预警和日报发送。",
                                List.of(),
                                List.of("审批结果邮件", "库存低水位预警", "日报/周报推送"),
                                List.of("SMTP Host", "SMTP Port", "发件邮箱", "授权码或应用密码")
                        ),
                        new IntegrationProviderItem(
                                "WECOM",
                                "企业微信",
                                "消息协同",
                                "RESERVED",
                                "保留消息通知和组织同步能力，后续可接入企业内部账号体系。",
                                List.of(),
                                List.of("审批待办提醒", "到货通知", "部门与成员同步", "企业微信登录"),
                                List.of("企业微信应用", "AgentId", "Secret", "回调地址")
                        ),
                        new IntegrationProviderItem(
                                "FEISHU",
                                "飞书",
                                "消息协同",
                                "RESERVED",
                                "保留审批、采购、组织同步等扩展位，当前不发起真实调用。",
                                List.of(),
                                List.of("审批提醒", "采购到货通知", "组织架构同步", "飞书登录"),
                                List.of("飞书应用", "App ID", "App Secret", "Encrypt Key", "事件订阅回调地址")
                        ),
                        new IntegrationProviderItem(
                                "DINGTALK",
                                "钉钉",
                                "消息协同",
                                "RESERVED",
                                "保留为候选通知与组织同步通道，当前不纳入主流程依赖。",
                                List.of(),
                                List.of("消息机器人通知", "审批同步提醒", "组织通讯录同步"),
                                List.of("钉钉应用", "AppKey", "AppSecret", "回调地址")
                        ),
                        new IntegrationProviderItem(
                                "MINIO",
                                "MinIO / 对象存储",
                                "文件存储",
                                "RESERVED",
                                "附件能力先走本地目录，后续可切换到对象存储而不改变业务数据结构。",
                                List.of("本地上传目录预留"),
                                List.of("审批附件存储", "资产图片存储", "单据文件归档"),
                                List.of("访问地址", "Bucket", "Access Key", "Secret Key")
                        ),
                        new IntegrationProviderItem(
                                "SSO",
                                "AD / LDAP / SSO",
                                "身份认证",
                                "RESERVED",
                                "保留统一身份接入层，避免未来重做账号、角色、数据权限模型。",
                                List.of("本地账号登录"),
                                List.of("企业单点登录", "组织账号同步", "统一认证映射"),
                                List.of("目录服务地址", "租户信息", "客户端密钥", "回调地址")
                        )
                )
        );
    }
}
