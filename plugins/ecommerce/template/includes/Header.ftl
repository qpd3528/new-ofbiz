<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script>
$(document).ready(function() {
  if (jQuery.fn.bsgdprcookies !== undefined) {
    jQuery('body').bsgdprcookies({
        title: '${uiLabelMap.EcommerceCookieConsentTitle}',
        message: '${uiLabelMap.EcommerceCookieConsentMessage}',
        moreLink: '/ecommerce/control/CookiePolicy',
        moreLinkLabel: ' ${uiLabelMap.EcommerceCookieConsentMoreLinkLabel}',
        acceptButtonLabel: '${uiLabelMap.EcommerceCookieConsentAcceptButtonLabel}',
        advancedButtonLabel: '${uiLabelMap.EcommerceCookieConsentAdvancedButtonLabel}',
        allowAdvancedOptions: false
    });
  }
});
</script>
<#--<div class="container-fluid">-->
<div class="readysay_header">
    <div class="wrap">
<#--    <div class="row align-items-center">-->
<#--      <div class="col">-->
        <div class="readysay_logo">
            <a href="<@ofbizUrl>main</@ofbizUrl>">
                <#if sessionAttributes.overrideLogo??>
                  <img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo"/>
                <#elseif catalogHeaderLogo??>
                  <img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo"/>
                <#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
                  <img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL}</@ofbizContentUrl>" alt="Logo"/>
                </#if>
            </a>
        </div>
        <div class="header_menus">
            <ul class="navbar-nav tr_50">
                <li class="nav-item ml-50"><a class="nav-link" href="<@ofbizUrl>news</@ofbizUrl>">${uiLabelMap.ReadySayNews}</a></li>
                <li class="nav-item ml-50" ><a class="nav-link" href="<@ofbizUrl>service</@ofbizUrl>">${uiLabelMap.ReadySayService}</a></li>
                <li class="nav-item ml-50"><a class="nav-link" href="<@ofbizUrl>promise</@ofbizUrl>">${uiLabelMap.ReadySayPromise}</a></li>
                <li class="nav-item ml-50"><a class="nav-link" href="<@ofbizUrl>brandStory</@ofbizUrl>">${uiLabelMap.ReadySayBrandStory}</a></li>
                <li class="nav-item ml-50"><a class="nav-link" href="<@ofbizUrl>manual</@ofbizUrl>">${uiLabelMap.ReadySayManual}</a></li>
            </ul>
        </div>
        <div class="language">
<#--            <div class="card-header">-->
<#--                ${uiLabelMap.CommonLanguageTitle}-->
<#--            </div>-->
<#--            <div class="card-body">-->
                <form method="post" name="chooseLanguage" action="<@ofbizUrl>setSessionLocale</@ofbizUrl>">
                    <select name="newLocale" class="selectBox form-control" onchange="submit()">
                        <#assign availableLocales = Static["org.apache.ofbiz.base.util.UtilMisc"].availableLocales()/>
                        <#list availableLocales as availableLocale>
                            <#assign langAttr = availableLocale.toString()?replace("_", "-")>
                            <#assign langDir = "ltr">
                            <#if "ar.iw"?contains(langAttr?substring(0, 2))>
                                <#assign langDir = "rtl">
                            </#if>
                            <option lang="${langAttr}" dir="${langDir}"
                                    value="${availableLocale.toString()}"<#if locale.toString() = availableLocale.toString()>
                                selected="selected"</#if>>
                                ${availableLocale.getDisplayName(availableLocale)}
                            </option>
                        </#list>
                    </select>
                </form>
        </div>
        <div class="mond_1"></div>
<#--      </div>-->
<#--      <div class="col text-center d-none d-lg-block">-->
<#--        <#if !productStore??>-->
<#--              <h3>${uiLabelMap.EcommerceNoProductStore}</h3>-->
<#--            </#if>-->
<#--            <#if (productStore.title)??>-->
<#--              <h3>${productStore.title}</h3>-->
<#--             </#if>-->
<#--            <#if (productStore.subtitle)??>-->
<#--              <div id="company-subtitle">${productStore.subtitle}</div>-->
<#--            </#if>-->
<#--            <div>-->
<#--              <#if sessionAttributes.autoName?has_content>-->
<#--                <span class="text-success">${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName}!</span>-->
<#--                (${uiLabelMap.CommonNotYou}?&nbsp;-->
<#--                <a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="linktext">${uiLabelMap.CommonClickHere}</a>)-->
<#--              <#else>-->
<#--                ${uiLabelMap.CommonWelcome}!-->
<#--              </#if>-->
<#--            </div>-->
<#--      </div>-->
<#--      <div class="col">-->
<#--        ${screens.render("component://ecommerce/widget/CartScreens.xml#microcart")}-->
<#--      </div>-->
<#--    </div>-->
    </div>
</div>

<#--<nav class="navbar navbar-expand-lg navbar-dark bg-dark">-->
<#--  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">-->
<#--    <span class="navbar-toggler-icon"></span>-->
<#--  </button>-->
<#--  <div class="collapse navbar-collapse" id="navbarContent">-->
<#--    <ul class="navbar-nav mr-auto">-->
<#--      <#if userLogin?has_content && userLogin.userLoginId != "anonymous">-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>-->
<#--        </li>-->
<#--      <#else>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a>-->
<#--        </li>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a>-->
<#--        </li>-->
<#--      </#if>-->
<#--      <li class="nav-item">-->
<#--        <#if userLogin?has_content && userLogin.userLoginId != "anonymous">-->
<#--          <a class="nav-link" href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a>-->
<#--        <#else>-->
<#--          <a class="nav-link" href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a>-->
<#--        </#if>-->
<#--      </li>-->
<#--      <li class="nav-item">-->
<#--        <a class="nav-link" href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.CommonMain}</a>-->
<#--      </li>-->
<#--    </ul>-->
<#--    <ul class="navbar-nav ml-auto">-->
<#--      <#if !userLogin?has_content || (userLogin.userLoginId)! != "anonymous">-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a>-->
<#--        </li>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.CommonMessages}</a>-->
<#--        </li>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>ListQuotes</@ofbizUrl>">${uiLabelMap.OrderOrderQuotes}</a>-->
<#--        </li>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>ListRequests</@ofbizUrl>">${uiLabelMap.OrderRequests}</a>-->
<#--        </li>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.EcommerceShoppingLists}</a>-->
<#--        </li>-->
<#--        <li class="nav-item">-->
<#--          <a class="nav-link" href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.EcommerceOrderHistory}</a>-->
<#--        </li>-->
<#--      </#if>-->
<#--      <#if catalogQuickaddUse>-->
<#--        <li class="nav-item"><a class="nav-link" href="<@ofbizUrl>quickadd</@ofbizUrl>">${uiLabelMap.CommonQuickAdd}</a></li>-->
<#--      </#if>-->
<#--    </ul>-->
<#--  </div>-->
<#--</nav>-->





