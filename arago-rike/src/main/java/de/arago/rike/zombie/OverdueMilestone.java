/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.arago.rike.zombie;

import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;

/**
 *
 * @author xg04123
 */
public class OverdueMilestone
{
  private final String milestoneId;
  private final String hoursLeft;

  public OverdueMilestone(String milestoneId, String hoursLeft)
  {
    this.milestoneId = milestoneId;
    this.hoursLeft   = hoursLeft;
  }
  
  public Milestone getMilestone()
  {
    return new DataHelperRike<Milestone>(Milestone.class).find(milestoneId);
  }  
  
  public int getHoursLeft()
  {
    try
    {
      return Integer.valueOf(hoursLeft, 10);
    } catch(NumberFormatException ignored) {
      return 0;
    } 
  }  
}
