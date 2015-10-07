/**
 * 
 */
/**
 * 
 * Instructions on how to start the sclaim_app (it is mandatory to complete steps in this exact order):
 * <ol>
 * 
 * <li>Run the command <./run.sh src-scenario/scenario/amilab/sclaim_app/scenario-distributed-server.xml "server client"
 * 9002 [SERVER_IP] 9002> on the server.
 * 
 * <li>Press "Create agents" on the server.
 * 
 * <li>Run the command <./run.sh src-scenario/scenario/amilab/sclaim_app/scenario-distributed-client1.xml "client" 9002
 * [SERVER_IP] 9002> on the first client.
 * 
 * <li>Press "Create agents" on the first client.
 * 
 * <li>Press "and Start" on the first client.
 * 
 * <li>Run the command <./run.sh src-scenario/scenario/amilab/sclaim_app/scenario-distributed-client2.xml "client" 9002
 * [SERVER_IP] 9002> on the second client.
 * 
 * <li>Press "Create agents" on the second client.
 * 
 * <li>Press "and Start" on the second client.
 * 
 * <li>Press "and Start" on the server.
 * 
 * <li>Move from computer to computer for about 10 seconds (this starts up the Kinects and removes old entries from the
 * Kestrel queue).
 * 
 * <li>Watch as the happy face travels to the closest computer to you!
 * 
 * </ol>
 * 
 * NOTES:
 * <ul>
 * 
 * <li>Replace [SERVER_IP] with the IP of the server.
 * 
 * <li>All computers must be in the same local network.
 * 
 * <li>The server is also a client (it can be called the third client).
 * 
 * </ul>
 * 
 * @author Claudiu-Mihai Toma
 */
package scenario.application.amilab_follow_me_v1;