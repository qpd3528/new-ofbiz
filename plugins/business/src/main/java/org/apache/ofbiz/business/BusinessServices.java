/*******************************************************************************
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
 *******************************************************************************/
package org.apache.ofbiz.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.websocket.Session;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import static org.apache.ofbiz.talk.TalkServices.getMessage;


public class BusinessServices {
    private static final String MODULE = BusinessServices.class.getName();

    public static Map<String, Object> sendBusinessPushNotifications(DispatchContext dctx, Map<String, ? extends Object> context) {
        String businessId = (String) context.get("businessId");
        String message = (String) context.get("message");
        Set<Session> clients = BusinessWebSockets.getClients();
        try {
            synchronized (clients) {
                for (Session client : clients) {
                    client.getBasicRemote().sendText(message + ": " + businessId);
                }
            }
        } catch (IOException e) {
            Debug.logError(e.getMessage(), MODULE);
        }
        return ServiceUtil.returnSuccess();
    }


    public static Map<String, Object> getBusinessInfoByMajorId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        String majorId = (String) context.get("majorId");


        List<GenericValue> list = null;
        if (majorId != null) {
            list = EntityQuery.use(delegator).from("Business").where("majorId", majorId)
                    .cache(true).queryList();
        } else {
            return ServiceUtil.returnError(getMessage());
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();

        result.put("list", list);

        return result;
    }

    public static Map<String, Object> getCompaniesByMajorId(DispatchContext dctx, Map<String, ?> context) {
        // Delegator 객체를 통해 데이터베이스 작업을 수행
        Delegator delegator = dctx.getDelegator();
        // Context에서 majorId 값을 가져옴
        String majorId = (String) context.get("majorId");
        // 결과를 저장할 Map 객체를 생성
        Map<String, Object> result = new HashMap<>();

        try {
            // majorId 값으로 businessItems 엔티티를 조회
            List<GenericValue> businessItems = delegator.findByAnd("BusinessItem", UtilMisc.toMap("majorId", majorId), null, false);
            if (businessItems.isEmpty()) {
                // 조회된 majorId를 가진 businessItems가 없으면 실패 메시지를 반환
                return ServiceUtil.returnFailure("No businessItems found for the given major_id");
            }

            // 조회된 businessItems 엔티티의 businessId 값을 사용하여 business 엔티티를 조회
            List<GenericValue> list = delegator.findList(
                    "Business",
                    EntityCondition.makeCondition("businessId", EntityOperator.IN, businessItems.stream().map(businessItem -> businessItem.get("businessId")).collect(Collectors.toList())),
                    null,
                    null,
                    null,
                    false
            );
            // 조회된 business 엔티티의 목록을 리스트에 추가
            result.put("list", list);
            // 성공 메시지를 반환
            return result;
        } catch (GenericEntityException e) {
            // 예외가 발생하면 에러 메시지를 반환
            return ServiceUtil.returnError("Error retrieving companies: " + e.getMessage());
        }
    }


}
