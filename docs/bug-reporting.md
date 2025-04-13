# Bug Reporting Guidelines and Jira Workflow

This document outlines the bug reporting process used by the ReelRec team during the development of the application. We integrated Jira as our primary tool for tracking, reporting, and managing bugs throughout our workflow.

## Overview

All bugs, feature requests, and improvement ideas were reported, triaged, and tracked using **Jira**. Our Jira instance was used to coordinate development efforts and ensure that issues were documented and resolved in a timely manner. This document describes our Jira workflow and how to reference Jira tickets in code and documentation.

## Jira Workflow

Our bug reporting process followed a standardized workflow that included the following stages:

1. **New / Open**  
   - When a bug was first reported, it was automatically assigned the status **"New"**.  
   - Tickets were created with detailed reproduction steps, expected behavior, and the actual observed behavior.

2. **Triage / In Progress**  
   - The development team reviewed new bug reports during daily stand-ups or scheduled triage meetings.  
   - Bugs were either prioritized or scheduled for the next sprint.  
   - Once a bug was assigned to a developer, its status changed to **"In Progress"**.

3. **Resolved / Fixed**  
   - After coding a fix, the ticket status was updated to **"Resolved"**.  
   - The resolution field included details about the fix.

4. **Verification / Closed**  
   - The QA team or a peer reviewer tested the fix.  
   - If the bug was confirmed as resolved, the status was changed to **"Closed"**.  
   - If further issues were discovered, the ticket could revert to **"In Progress"** or **"Reopened"**.

## Jira Tickets and Documentation
All Jira tickets and issues are available in the [Jira HTML Document](jira.html). This document provides a comprehensive overview of all reported bugs, their statuses, and any relevant comments or discussions.
- **Ticket ID**: Each ticket has a unique identifier (e.g., `REEL-123`) that should be referenced in code comments and documentation.
- **Ticket Summary**: A brief description of the issue or feature request.
- **Status**: The current status of the ticket (e.g., New, In Progress, Resolved, Closed).
- **Assignee**: The developer responsible for resolving the issue.
- **Priority**: The priority level of the ticket (e.g., Low, Medium, High).
- **Labels**: Tags used to categorize tickets (e.g., bug, feature, improvement).
- **Comments**: Any discussions or notes related to the ticket.
