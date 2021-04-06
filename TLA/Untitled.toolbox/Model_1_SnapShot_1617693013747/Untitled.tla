------------------------------ MODULE Untitled ------------------------------

EXTENDS TLC, Integers, Sequences
CONSTANTS TotUpdates

(*--algorithm getUpdate
variables
  queue = <<>>;
  canUpdate = 1;
  printed = 0;
  updatesToDo = TotUpdates;
define
  ProperFinalValue == <>(printed = 1)
end define;

process wantToUpdate \in { "up1", "up2" } 
variable item = "item";
begin updating:
  while updatesToDo > 0 do
    requestUpdate:
        await canUpdate = 1;
        queue := Append(queue, item);
    releaseUpdate: 
        await canUpdate = 1;
        item := Head(queue);
        updatesToDo := updatesToDo - 1;
  end while;
end process;

process wantToget \in { "get1", "get2" }
variable item = "none";
begin getting:
  while TRUE do
    requestGet: 
        await queue = <<>>;
        canUpdate := 0;
        printed := 1;
    releaseGet:
        canUpdate := 1
  end while;
end process;
end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "7bedff46" /\ chksum(tla) = "267e980a")
\* Process variable item of process wantToUpdate at line 17 col 10 changed to item_
VARIABLES queue, canUpdate, printed, updatesToDo, pc

(* define statement *)
ProperFinalValue == <>(printed = 1)

VARIABLES item_, item

vars == << queue, canUpdate, printed, updatesToDo, pc, item_, item >>

ProcSet == ({ "up1", "up2" }) \cup ({ "get1", "get2" })

Init == (* Global variables *)
        /\ queue = <<>>
        /\ canUpdate = 1
        /\ printed = 0
        /\ updatesToDo = TotUpdates
        (* Process wantToUpdate *)
        /\ item_ = [self \in { "up1", "up2" } |-> "item"]
        (* Process wantToget *)
        /\ item = [self \in { "get1", "get2" } |-> "none"]
        /\ pc = [self \in ProcSet |-> CASE self \in { "up1", "up2" } -> "updating"
                                        [] self \in { "get1", "get2" } -> "getting"]

updating(self) == /\ pc[self] = "updating"
                  /\ IF updatesToDo > 0
                        THEN /\ pc' = [pc EXCEPT ![self] = "requestUpdate"]
                        ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                  /\ UNCHANGED << queue, canUpdate, printed, updatesToDo, 
                                  item_, item >>

requestUpdate(self) == /\ pc[self] = "requestUpdate"
                       /\ canUpdate = 1
                       /\ queue' = Append(queue, item_[self])
                       /\ pc' = [pc EXCEPT ![self] = "releaseUpdate"]
                       /\ UNCHANGED << canUpdate, printed, updatesToDo, item_, 
                                       item >>

releaseUpdate(self) == /\ pc[self] = "releaseUpdate"
                       /\ canUpdate = 1
                       /\ item_' = [item_ EXCEPT ![self] = Head(queue)]
                       /\ updatesToDo' = updatesToDo - 1
                       /\ pc' = [pc EXCEPT ![self] = "updating"]
                       /\ UNCHANGED << queue, canUpdate, printed, item >>

wantToUpdate(self) == updating(self) \/ requestUpdate(self)
                         \/ releaseUpdate(self)

getting(self) == /\ pc[self] = "getting"
                 /\ pc' = [pc EXCEPT ![self] = "requestGet"]
                 /\ UNCHANGED << queue, canUpdate, printed, updatesToDo, item_, 
                                 item >>

requestGet(self) == /\ pc[self] = "requestGet"
                    /\ queue = <<>>
                    /\ canUpdate' = 0
                    /\ printed' = 1
                    /\ pc' = [pc EXCEPT ![self] = "releaseGet"]
                    /\ UNCHANGED << queue, updatesToDo, item_, item >>

releaseGet(self) == /\ pc[self] = "releaseGet"
                    /\ canUpdate' = 1
                    /\ pc' = [pc EXCEPT ![self] = "getting"]
                    /\ UNCHANGED << queue, printed, updatesToDo, item_, item >>

wantToget(self) == getting(self) \/ requestGet(self) \/ releaseGet(self)

Next == (\E self \in { "up1", "up2" }: wantToUpdate(self))
           \/ (\E self \in { "get1", "get2" }: wantToget(self))

Spec == Init /\ [][Next]_vars

\* END TRANSLATION 

=============================================================================
\* Modification History
\* Last modified Tue Apr 06 09:10:03 CEST 2021 by asialucchi
\* Created Fri Apr 02 15:50:24 CEST 2021 by asialucchi
