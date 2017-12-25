package com.harmony.umbrella.log.template;

import java.util.List;

import com.harmony.umbrella.log.detector.MemberDetectors;

/**
 * @author wuxii@foxmail.com
 */
public class KeyWordTokenResolver implements TokenResolver {

    private MemberDetectors detectors;

    public KeyWordTokenResolver() {
    }

    public KeyWordTokenResolver(MemberDetectors detectors) {
        this.detectors = detectors;
    }

    @Override
    public boolean support(ScopeToken scopeToken) {
        return scopeToken.getKeyWord() != null;
    }

    @Override
    public Object resolve(ScopeToken scopeToken, LoggingContext context) {
        KeyWord keyWord = scopeToken.getKeyWord();
        Object keyObject = keyWord.resolve(context);
        Object resultObject = keyObject;
        List<String> items = scopeToken.getToken().getItems();
        int size = items.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                if (resultObject == null) {
                    break;
                }
                resultObject = detectors.get(items.get(i), resultObject);
            }
        }
        return resultObject;
    }

    public MemberDetectors getMemberDetectors() {
        return detectors;
    }

    public void setMemberDetectors(MemberDetectors memberDetectors) {
        detectors = memberDetectors;
    }

}
