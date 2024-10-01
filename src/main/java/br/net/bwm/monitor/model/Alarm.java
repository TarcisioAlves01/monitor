package br.net.bwm.monitor.model;

import java.util.Objects;

public class Alarm {

    private String criticality;
    private String alarm;
    private String device;
    private String laction;

    public Alarm(String alarm, String device, String criticality, String laction) {

        this.alarm = alarm;
        this.device = device;
        this.laction = laction;
        this.criticality = criticality;
    }

    @Override
    public String toString() {
        return "Alarm [criticality=" + criticality + ", alarm=" + alarm + ", device=" + device + ", laction=" + laction
                + "]";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.alarm);
        hash = 67 * hash + Objects.hashCode(this.device);
        hash = 67 * hash + Objects.hashCode(this.laction);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Alarm other = (Alarm) obj;
        if (!Objects.equals(this.alarm, other.alarm)) {
            return false;
        }
        if (!Objects.equals(this.device, other.device)) {
            return false;
        }
      
        return Objects.equals(this.laction, other.laction);
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLaction() {
        return laction;
    }

    public void setLaction(String laction) {
        this.laction = laction;
    } 

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }

}
