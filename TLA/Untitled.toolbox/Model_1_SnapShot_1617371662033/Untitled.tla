------------------------------ MODULE Untitled ------------------------------

EXTENDS TLC, Integers, Sequences
CONSTANTS UpdatesToDo

(*--algorithm getUpdate
variables
  queue = <<>>;
  canUpdate = 1;
  printed = 0;
  updatesToDo = UpdatesToDo;
define
  ProperFinalValue == <>(printed = 1)
end define;

process wantToUpdate \in { "up1", "up2" } 
variable item = "";
begin updating:
  while updatesToDo > 0 do
    request:
        await canUpdate = 1;
        queue := Append(queue, item);
    release: 
        await canUpdate = 1;
        item := Head(queue);
        updatesToDo := updatesToDo - 1;
  end while;
end process;

process wantToget \in { "get1", "get2" }
variable item = "none";
begin Consume:
  while TRUE do
    request: 
        await queue = <<>>;
        canUpdate := 0;
        printed := 1;
    release:
        canUpdate := 1
  end while;
end process;
end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "df699c47" /\ chksum(tla) = "9261682")
\* Label request of process wantToUpdate at line 21 col 9 changed to request_
\* Label release of process wantToUpdate at line 24 col 9 changed to release_
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
        /\ updatesToDo = UpdatesToDo
        (* Process wantToUpdate *)
        /\ item_ = [self \in { "up1", "up2" } |-> ""]
        (* Process wantToget *)
        /\ item = [self \in { "get1", "get2" } |-> "none"]
        /\ pc = [self \in ProcSet |-> CASE self \in { "up1", "up2" } -> "updating"
                                        [] self \in { "get1", "get2" } -> "Consume"]

updating(self) == /\ pc[self] = "updating"
                  /\ IF updatesToDo > 0
                        THEN /\ pc' = [pc EXCEPT ![self] = "request_"]
                        ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                  /\ UNCHANGED << queue, canUpdate, printed, updatesToDo, 
                                  item_, item >>

request_(self) == /\ pc[self] = "request_"
                  /\ canUpdate = 1
                  /\ queue' = Append(queue, item_[self])
                  /\ pc' = [pc EXCEPT ![self] = "release_"]
                  /\ UNCHANGED << canUpdate, printed, updatesToDo, item_, item >>

release_(self) == /\ pc[self] = "release_"
                  /\ canUpdate = 1
                  /\ item_' = [item_ EXCEPT ![self] = Head(queue)]
                  /\ updatesToDo' = updatesToDo - 1
                  /\ pc' = [pc EXCEPT ![self] = "updating"]
                  /\ UNCHANGED << queue, canUpdate, printed, item >>

wantToUpdate(self) == updating(self) \/ request_(self) \/ release_(self)

Consume(self) == /\ pc[self] = "Consume"
                 /\ pc' = [pc EXCEPT ![self] = "request"]
                 /\ UNCHANGED << queue, canUpdate, printed, updatesToDo, item_, 
                                 item >>

request(self) == /\ pc[self] = "request"
                 /\ queue = <<>>
                 /\ canUpdate' = 0
                 /\ printed' = 1
                 /\ pc' = [pc EXCEPT ![self] = "release"]
                 /\ UNCHANGED << queue, updatesToDo, item_, item >>

release(self) == /\ pc[self] = "release"
                 /\ canUpdate' = 1
                 /\ pc' = [pc EXCEPT ![self] = "Consume"]
                 /\ UNCHANGED << queue, printed, updatesToDo, item_, item >>

wantToget(self) == Consume(self) \/ request(self) \/ release(self)

Next == (\E self \in { "up1", "up2" }: wantToUpdate(self))
           \/ (\E self \in { "get1", "get2" }: wantToget(self))

Spec == Init /\ [][Next]_vars

\* END TRANSLATION 

=============================================================================
\* Modification History
\* Last modified Fri Apr 02 15:53:07 CEST 2021 by asialucchi
\* Created Fri Apr 02 15:50:24 CEST 2021 by asialucchi
