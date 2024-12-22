# Made by Emperorc
import sys
from com.l2jprime.gameserver.model.quest import State
from com.l2jprime.gameserver.model.quest import QuestState
from quests.SagasSuperclass import Quest as JQuest

qn = "99_SagaOfTheFortuneSeeker"
qnu = 99
qna = "Saga of the Fortune Seeker"

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     # first initialize the quest.  The superclass defines variables, instantiates States, etc
     JQuest.__init__(self,id,name,descr)
     # Next, override necessary variables:
     self.NPC = [31594,31623,31600,31600,31601,31646,31649,31650,31654,31655,31657,31600]
     self.Items = [7080,7608,7081,7514,7297,7328,7359,7390,7421,7452,7109,0]
     self.Mob = [27259,27248,27309]
     self.qn = qn
     self.classid = 117
     self.prevclass = 0x37
     self.X = [191046,46066,46087]
     self.Y = [-40640,-36396,-36372]
     self.Z = [-3042,-1685,-1685]
     self.Text = ["PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
                  "... Oh ... good! So it was ... let's begin!","I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
                  "Paying homage to those who disrupt the orderly will be PLAYERNAME's death!","Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
                  "Why do you interfere others' battles?","This is a waste of time.. Say goodbye...!","...That is the enemy",
                  "...Goodness! PLAYERNAME you are still looking?","PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
                  "Your sword is not an ornament. Don't you think, PLAYERNAME?","Goodness! I no longer sense a battle there now.","let...","Only engaged in the battle to bar their choice. Perhaps you should regret.",
                  "The human nation was foolish to try and fight a giant's strength.","Must...Retreat... Too...Strong.","PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker","....! Fight...Defeat...It...Fight...Defeat...It..."]
     # finally, register all events to be triggered appropriately, using the overriden values.
     JQuest.registerNPCs(self)

QUEST       = Quest(qnu,qn,qna)

QUEST.setInitialState(QUEST.CREATED)