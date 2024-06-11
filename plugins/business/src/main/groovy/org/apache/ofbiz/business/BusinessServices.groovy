/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package org.apache.ofbiz.business

import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.util.EntityUtilProperties
import org.apache.ofbiz.service.ServiceUtil
import org.apache.ofbiz.webapp.control.JWTManager

import java.sql.Timestamp

Map prodCatalogToPartyPermissionCheck() {
    parameters.altPermission = 'PARTYMGR'
    return catalogPermissionCheck()
}

/**
 * create missing category and product alternative urls
 */
Map createMissingCategoryAndProductAltUrls() {
    Timestamp now = UtilDateTime.nowTimestamp()
    Map result = success()
    result.prodCatalogId = parameters.prodCatalogId
    int categoriesNotUpdated = 0
    int categoriesUpdated = 0
    int productsNotUpdated = 0
    int productsUpdated = 0
    String checkProduct = parameters.product
    String checkCategory = parameters.category
    List prodCatalogCategoryList = from('ProdCatalogCategory').where('prodCatalogId', parameters.prodCatalogId).queryList()

    // get all categories
    List productCategories = []
    for (GenericValue prodCatalog: prodCatalogCategoryList) {
        productCategories.addAll(createMissingCategoryAltUrlInline(prodCatalog.productCategoryId))
    }

    for (Map productCategoryList : productCategories) {
        // create product category alternative URLs
        if (checkCategory) {
            List productCategoryContentAndInfoList = from('ProductCategoryContentAndInfo')
                    .where('productCategoryId', productCategoryList.productCategoryId,
                            'prodCatContentTypeId', 'ALTERNATIVE_URL')
                    .filterByDate()
                    .orderBy('-fromDate')
                    .cache()
                    .queryList()
            if (productCategoryContentAndInfoList) {
                categoriesNotUpdated += 1
            } else {
                Map createSimpleTextContentForCategoryCtx = [
                        fromDate: now,
                        prodCatContentTypeId: 'ALTERNATIVE_URL',
                        localeString: 'en',
                        productCategoryId: productCategoryList.productCategoryId
                ]
                if (productCategoryList.categoryName) {
                    createSimpleTextContentForCategoryCtx.text = productCategoryList.categoryName
                } else {
                    GenericValue productCategoryContent = from('ProductCategoryContentAndInfo')
                            .where('productCategoryId', productCategoryList.productCategoryId,
                                    'prodCatContentTypeId', 'CATEGORY_NAME')
                            .filterByDate()
                            .orderBy('-fromDate')
                            .cache()
                            .queryFirst()
                    if (productCategoryContent) {
                        Map gcadrRes = run service: 'getContentAndDataResource', with: [contentId: productCategoryContent.contentId]
                        Map resultMap = gcadrRes.resultData
                        createSimpleTextContentForCategoryCtx.text = resultMap.electronicText.textData
                    }
                }
                if (createSimpleTextContentForCategoryCtx.text) {
                    Map res = run service: 'createSimpleTextContentForCategory', with: createSimpleTextContentForCategoryCtx
                    if (ServiceUtil.isError(res)) {
                        return res
                    }
                    categoriesUpdated += 1
                }
            }
        }

        // create product alternative URLs
        if (checkProduct) {
            List productCategoryMemberList = from('ProductCategoryMember')
                    .where('productCategoryId', productCategoryList.productCategoryId)
                    .filterByDate()
                    .orderBy('-fromDate')
                    .cache()
                    .queryList()
            productCategoryMemberList.each { Map productCategoryMember ->
                String memberProductId = productCategoryMember.productId
                List productContentAndInfoList = from('ProductContentAndInfo')
                        .where('productId', memberProductId, 'productContentTypeId', 'ALTERNATIVE_URL')
                        .cache().orderBy('-fromDate')
                        .filterByDate()
                        .queryList()
                if (productContentAndInfoList) {
                    productsNotUpdated += 1
                } else {
                    GenericValue product = from('Product').where('productId', memberProductId).queryOne()
                    Map createSimpleTextContentForProductCtx = [
                            fromDate: now,
                            productContentTypeId: 'ALTERNATIVE_URL',
                            localeString: 'en',
                            productId: memberProductId
                    ]
                    createSimpleTextContentForProductCtx.text = product.internalName ?: product.productName
                    if (createSimpleTextContentForProductCtx.text) {
                        Map res = run service: 'createSimpleTextContentForProduct', with: createSimpleTextContentForProductCtx
                        if (ServiceUtil.isError(res)) {
                            return res
                        }
                        productsUpdated += 1
                    }
                }
            }
        }
    }
    result.successMessageList = [
            "Categories updated: ${categoriesUpdated}",
            "Products updated: ${productsUpdated}"
    ]
    result.categoriesNotUpdated = categoriesNotUpdated
    result.productsNotUpdated = productsNotUpdated
    result.categoriesUpdated = categoriesUpdated
    result.productsUpdated = productsUpdated
    return result
}

List createMissingCategoryAltUrlInline(String parentProductCategoryId) {
    List productCategoryRollupIds = from('ProductCategoryRollup')
            .where('parentProductCategoryId', parentProductCategoryId)
            .filterByDate()
            .cache()
            .getFieldList('productCategoryId')
    List productCategories = []
    for (String productCategoryId : productCategoryRollupIds) {
        // append product category to list
        productCategories << from('ProductCategory')
                .where('productCategoryId', productCategoryId)
                .cache()
                .queryOne()

        // find rollup product categories
        productCategories.addAll(createMissingCategoryAltUrlInline(productCategoryId))
    }
    return productCategories
}
