# Development Approach

This document defines the development workflows and policies for an agile, free and goal driven approach like we at arago use for our product development. 

## Note

This approach is an **example** how rike can be used. The process should always be adapted to the team. Or it may even be completely different. It is up to the team to decide where to put task descriptions, how to record requirements, how to communicate, etc pp.


## 1 Modes of Operation

There are two modes for delivering projects: "Innovation Machine" and "Emergency".

The goal of the "Innovation Machine" is to guarantee a defined degree of quality and finality. And the goal of the "Emergency" mode is to unfolde the maximum velocity in dealing with one major set of tasks. 

with our „RIKE“ tool, we achieve transparency in planning and delivery of projects. But questions like „Why has something been done?” and „Has the goal been achieved?“ remain unanswered. In order to achieve transparency and traceability in these areas as well, specific procedural structures must be implemented and certain formalities regarding single steps have to be adhered to.

The development approach for "Emergency" mode is the pure tool driven development supported by "RIKE" and the approach for "Innovation Machine" extends the "RIKE" process to cover the full life cycle. 

## 2 "Emergency" Mode 

"Emergency" is equivalent to the pure "RIKE" process, where requirements are never formally captured, but known requirements are broken down into tasks and milestones immediately which are then being delivered with highest priority. 
Examples for such a mode: Solving a critical error, urgent features for a customer. 
This mode enables a team to stay flexible but disables long-term planning. Therefore, "Emergency" projects should never span more than a week.

## 3 Steps of the "Innovation Machine"

For the "Innovation Machine", the process always requires the following steps:

1. Requirements Capturing
1. Design
1. Implementation
1. Quality Assurance

Progressing to the next step is only permitted once the prior step has been finalised and all relevant documents have been created and reviewed. If changes to the requirements or design are necessary at a later stage, or if the quality assurance identifies errors in design or implementation, returning to the respective step is required.

There are two potential outcomes:

+ success: 
if the last step of the development cycle has been finalised the project is considered successful. Subsequently, the version number of the respective product is increased and the implemented changes are captured in the respective change log.
+ failure: 
if a project is stalled, outdated, unwanted or unnecessary, it is stopped and classified as failed.

The analysis regarding the number of projects which have been successful, have failed or are ongoing will give you a rough measure of a team’s effectiveness.

### A) Requirements Capturing
A project is given a name and a short description. A WiKi page is established for it. The [requirements.pdf](https://raw.github.com/arago/rike/master/requirements.pdf) needs to be filled and attached to the wiki page and then sent to “customer”. The content of the file is agreed in one or several iterations. The result of this step is a requirements document that has been “approved” by both sides. If such approval cannot be achieved within a month this incarnation of the project is considered a failure. 

### B) Design
In the design phase, the implementation of a release is planned on a single task basis. The result is:

1. Milestone- / release-planning in "RIKE" (with exact effort estimates).
2. Sufficiently detailed description of individual implementation tasks in WiKi or other tools.
3. The UML-diagrams of the architecture (for interfaces and connectors).
4. Click-prototype or PowerPoint presentation for new UI-elements.
5. A file that lists the documents mentioned above which has been reviewed and approved by customer.

### C) Implementation
All tasks which have been planned in "RIKE" are completed.

### D) Quality Assurance
The results are individually checked against requirements. The planned test scope is ascertained.