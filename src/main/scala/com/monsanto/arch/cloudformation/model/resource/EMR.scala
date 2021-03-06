package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token}
import spray.json.{DefaultJsonProtocol, JsObject, JsValue, JsonFormat, RootJsonFormat}
import DefaultJsonProtocol._


case class Application(
                        AdditionalInfo: Option[Map[String, Token[String]]],
                        Args: Option[Seq[Token[String]]],
                        Name: Option[Token[String]],
                        Version: Option[Token[String]]
                      )

object Application {
  implicit val format = jsonFormat4(Application.apply)
}

case class ScriptBootstrapAction(Args: Option[Seq[Token[String]]], Path: Token[String])
object ScriptBootstrapAction {
  implicit val format = jsonFormat2(ScriptBootstrapAction.apply)
}

case class BootstrapAction(Name: Token[String], ScriptBootstrapAction: ScriptBootstrapAction)
object BootstrapAction {
  implicit val format = jsonFormat2(BootstrapAction.apply)
}

case class ClusterConfiguration(Classification: Option[Token[String]],
                                ConfigurationProperties: Option[Map[String, Token[String]]],
                                Configurations: Option[Seq[ClusterConfiguration]]
                               )
object ClusterConfiguration {
  implicit object format extends RootJsonFormat[ClusterConfiguration] {
    override def write(obj: ClusterConfiguration): JsValue = {
      val classification = obj.Classification.map(implicitly[JsonFormat[Token[String]]].write)
      val configurationProperties = obj.ConfigurationProperties.map(implicitly[JsonFormat[Map[String, Token[String]]]].write)
      lazy val seqWrite = implicitly[RootJsonFormat[Seq[ClusterConfiguration]]]
      val configurations = obj.Configurations.map(seqWrite.write)
      JsObject(Seq(
        "Classification" -> classification,
        "ConfigurationProperties" -> configurationProperties,
        "Configurations" -> configurations
      ).flatMap(t => t._2.map(t._1 -> _).toSeq)
        .toMap)
    }

    override def read(json: JsValue): ClusterConfiguration = ???
  }
}

case class VolumeSpecification(Iops: Option[Token[Int]], SizeInGB: Token[Int], VolumeType: Token[String])
object VolumeSpecification {
  implicit val format = jsonFormat3(VolumeSpecification.apply)
}

case class EbsBlockDeviceConfig(VolumeSpecification: VolumeSpecification, VolumesPerInstance: Option[Token[Int]])
object EbsBlockDeviceConfig {
  implicit val format = jsonFormat2(EbsBlockDeviceConfig.apply)
}

case class EbsConfiguration(EbsBlockDeviceConfigs: Option[Seq[EbsBlockDeviceConfig]],
                            EbsOptimized: Option[Token[Boolean]])
object EbsConfiguration {
  implicit val format = jsonFormat2(EbsConfiguration.apply)
}

case class InstanceGroupConfig(
                                BidPrice: Option[Token[String]],
                                Configurations: Option[Seq[ClusterConfiguration]],
                                EbsConfiguration: Option[EbsConfiguration],
                                InstanceCount: Token[Int],
                                InstanceType: Token[String],
                                Market: Option[Token[String]],
                                Name: Option[Token[String]]
                              )
object InstanceGroupConfig {
  implicit val format = jsonFormat7(InstanceGroupConfig.apply)
}

case class PlacementType(AvailabilityZone: String)
object PlacementType {
  implicit val format = jsonFormat1(PlacementType.apply)
}

case class JobFlowInstancesConfig(AdditionalMasterSecurityGroups: Option[Seq[Token[String]]],
                                  AdditionalSlaveSecurityGroups: Option[Seq[Token[String]]],
                                  CoreInstanceGroup: InstanceGroupConfig,
                                  Ec2KeyName: Option[Token[String]],
                                  Ec2SubnetId: Option[Token[String]],
                                  EmrManagedMasterSecurityGroup: Option[Token[String]],
                                  EmrManagedSlaveSecurityGroup: Option[Token[String]],
                                  HadoopVersion: Option[Token[String]],
                                  MasterInstanceGroup: InstanceGroupConfig,
                                  Placement: Option[PlacementType],
                                  ServiceAccessSecurityGroup: Option[Token[String]],
                                  TerminationProtected: Option[Token[Boolean]]
                                 )
object JobFlowInstancesConfig {
  implicit val format = jsonFormat12(JobFlowInstancesConfig.apply)
}

case class `AWS::EMR::Cluster`(name: String,
                               AdditionalInfo: Option[JsValue],
                               Applications: Option[Seq[Application]],
                               BootstrapActions: Option[Seq[BootstrapAction]],
                               Configurations: Option[Seq[ClusterConfiguration]],
                               Instances: JobFlowInstancesConfig,
                               JobFlowRole: Token[String],
                               LogUri: Option[Token[String]],
                               Name: Token[String],
                               ReleaseLabel: Option[Token[String]],
                               ServiceRole: Token[String],
                               Tags: Option[Seq[AmazonTag]],
                               VisibileToAllUsers: Option[Token[Boolean]],
                               override val Condition: Option[ConditionRef] = None
                              ) extends Resource[`AWS::EMR::Cluster`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::EMR::Cluster` = copy(
    Condition = newCondition
  )
}
object `AWS::EMR::Cluster` {
  implicit val format : JsonFormat[`AWS::EMR::Cluster`] = jsonFormat14(`AWS::EMR::Cluster`.apply)
}
