package com.harmony.umbrella.log.detector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.core.OrderComparator;

/**
 * @author wuxii@foxmail.com
 */
public class MemberDetectors implements MemberDetector {

    private List<MemberDetector> detectors;

    public MemberDetectors() {
        this.detectors = new ArrayList<>();
    }

    public MemberDetectors(MemberDetector... detectors) {
        this.setMemberDetectors(Arrays.asList(detectors));
    }

    public MemberDetectors(List<MemberDetector> detectors) {
        this.setMemberDetectors(detectors);
    }

    @Override
    public boolean support(Class<?> type) {
        return getMemberDetector(type) != null;
    }

    @Override
    public Object get(String memberName, Object target) {
        MemberDetector detector = getMemberDetector(target.getClass());
        if (detector == null) {
            throw new IllegalArgumentException("unsupported member detector " + memberName + " of " + target);
        }
        return detector.get(memberName, target);
    }

    protected MemberDetector getMemberDetector(Class<?> type) {
        for (MemberDetector detector : detectors) {
            if (detector.support(type)) {
                return detector;
            }
        }
        return null;
    }

    public List<MemberDetector> getMemberDetectors() {
        return detectors;
    }

    public void setMemberDetectors(List<MemberDetector> detectors) {
        this.detectors = new ArrayList<>(detectors);
        OrderComparator.sort(this.detectors);
    }

    public void addMemberDetectors(MemberDetector... detectors) {
        Collections.addAll(this.detectors, detectors);
        OrderComparator.sort(this.detectors);
    }

    public static MemberDetectors allDetectors() {
        List<MemberDetector> list = new ArrayList<>();
        list.add(new HttpMemberDetector());
        list.add(new ListMemberDetector());
        list.add(new MapMemberDetector());
        list.add(new ArrayMemberDetector());
        list.add(new AnyMemberDetector());
        return new MemberDetectors(list);
    }

}
